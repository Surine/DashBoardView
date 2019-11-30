package cn.surine.dashboardview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * Intro：仪表盘View
 *
 * @author sunliwei
 * @date 2019-11-28 10:40
 */
public class DashBoardView extends View {

    /**
     * 起始角度 （以View中心为圆心，竖直向下为0度，顺时针增加至360度，起始角度最小为0）
     */
    private int startDegree;

    /**
     * 终止角度 （终止角度大于起始角度，最大为360）
     */
    private int endDegree;


    /**
     * 单位旋转角度
     */
    private int slotDegree;

    /**
     * 阶梯值
     */
    private String[] ladderValue = {"1", "1500", "3000", "4500", "6000"};


    /**
     * 背景色
     */
    private int backgroundColor;


    /**
     * 前景色
     */
    private int foregroundColor;


    /**
     * 圆环宽度
     * 普通值的UI
     */
    private float ringWidth;


    /**
     * 元素宽度
     */
    private float ringElementHeight = 10;


    /**
     * 强调宽度
     * ladder值的UI
     */
    private float emphasisWidth;


    /**
     * 当前进度
     * 0 <= progress <= 1
     */
    private float progress;


    /**
     * 全局padding
     */
    private float padding = 30;


    /**
     * 是否允许平衡自适应
     */
    private boolean banAdaptive;


    /**
     * 动画时间
     */
    private long animDuration = 400;


    /**
     * 插值器
     */
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();


    /*单个元素路径,默认+强调*/
    private Path path;
    private Path path2;
    //阶梯数
    private int ladderNum;
    //前景画笔，背景画笔
    private Paint backPaint;
    private Paint forePaint;
    //宽高
    private int width;
    private int height;
    //动画进度
    private float animProgress;
    //圆环半径
    private float radius;
    //真实起始角度
    private int realStartDegree;
    //强调元素 值和关系
    private Map<Integer, String> realEmphasisDegree = new HashMap<>();


    public DashBoardView(Context context) {
        this(context, null);
    }

    public DashBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DashBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashBoardView);
        backgroundColor = typedArray.getColor(R.styleable.DashBoardView_backgroundColor, Color.BLACK);
        foregroundColor = typedArray.getColor(R.styleable.DashBoardView_foregroundColor, Color.RED);
        startDegree = typedArray.getInteger(R.styleable.DashBoardView_startDegree, 50);
        endDegree = typedArray.getInteger(R.styleable.DashBoardView_endDegree, 310);
        slotDegree = typedArray.getInteger(R.styleable.DashBoardView_slotDegree, 4);
        ringWidth = typedArray.getDimension(R.styleable.DashBoardView_ringWidth, 30);
        emphasisWidth = typedArray.getDimension(R.styleable.DashBoardView_emphasisWidth, 40);
        typedArray.recycle();
        init();
    }

    private void init() {
        initData();
        initPaintBackground();
        initPaintForeground();
    }


    //初始化数据，这里初始化一些post刷新所不能识别到的变量
    private void initData() {
        //清空管理map
        realEmphasisDegree.clear();
        //对阶梯数赋值
        ladderNum = ladderValue.length - 1;
        //数据合法性检查
        degreeCheck();
    }

    //前景画笔
    private void initPaintForeground() {
        forePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        forePaint.setColor(backgroundColor);
        forePaint.setStyle(Paint.Style.FILL);
        forePaint.setTextSize(30);
        forePaint.setTextAlign(Paint.Align.CENTER);
    }

    //背景画笔
    private void initPaintBackground() {
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(backgroundColor);
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setTextSize(30);
        backPaint.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int normal = 700;
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(normal, normal);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(normal, MeasureSpec.getSize(heightMeasureSpec));
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), normal);
        }
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //初始化小元素路径,必须测量后才能初始化
        initPath();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawText(canvas);
        drawProgress(canvas);
    }


    //绘制文字
    private void drawText(Canvas canvas) {
        //恢复画布初始配置
        canvas.restore();
        //再次保存画布初始配置
        canvas.save();

        for (Map.Entry<Integer, String> entry : realEmphasisDegree.entrySet()) {
            canvas.save();
            canvas.translate((float) (width / 2 - (radius - 50) * Math.sin(Math.toRadians(entry.getKey()))), (float) (height / 2 + (radius - 50) * Math.cos(Math.toRadians(entry.getKey()))));
            canvas.drawText(entry.getValue(), 0, 0, backPaint);
            canvas.restore();
        }
    }


    //绘制进度
    private void drawProgress(Canvas canvas) {
        //恢复画布初始状态
        canvas.restore();
        //找到画笔起点，并配置格子数
        int bounds = readyToDraw(canvas);
        //自适应后的格子数
        int realBounds = bounds - ((ladderNum != 0) ? (bounds % ladderNum) : 0);
        //实际多少格
        int progressBounds = (int) ((banAdaptive ? bounds : realBounds) * (animProgress > 1 ? 1 : animProgress));
        drawCore(canvas, progressBounds, bounds, forePaint);
    }


    //绘制底层
    private void drawArc(Canvas canvas) {
        //保存画布初始配置
        canvas.save();
        //找到画笔起点，并配置格子数
        int bounds = readyToDraw(canvas);
        //自适应后的格子数
        int realBounds = bounds - ((ladderNum != 0) ? (bounds % ladderNum) : 0);
        drawCore(canvas, (banAdaptive ? bounds : realBounds), bounds, backPaint);
    }


    // 设置平衡自适应
    private int readyToDraw(Canvas canvas) {
        int bounds = (endDegree - startDegree) / slotDegree;
        canvas.translate(width / 2, height / 2);
        //旋转起始角度大小
        if (banAdaptive) {
            realStartDegree = startDegree;
        } else {
            //取余自适应
            int pickUpAdaptive = 0;
            if (ladderNum != 0) {
                pickUpAdaptive = (bounds % ladderNum) * slotDegree / 2;
            }
            //旋转角自适应
            int degreeSubAdaptive = (360 - endDegree - startDegree) / 2;
            realStartDegree = startDegree + pickUpAdaptive + degreeSubAdaptive;
        }
        canvas.rotate(realStartDegree);
        return bounds;
    }


    /**
     * 核心源码
     *
     * @param canvas
     * @param limitBounds     处理后的最大格子数
     * @param unHandlerBounds 未经处理的最大格子数
     */
    private void drawCore(Canvas canvas, int limitBounds, int unHandlerBounds, Paint paint) {
        for (int i = 0, j = 0; i <= limitBounds; i++) {
            if (ladderNum != 0 && i % (unHandlerBounds / ladderNum) == 0) {
                if (limitBounds != 0) {
                    canvas.drawPath(path2, paint);
                }
                if (!realEmphasisDegree.containsKey(realStartDegree + i * slotDegree)) {
                    realEmphasisDegree.put(realStartDegree + i * slotDegree, ladderValue[j]);
                    j++;
                }
            } else {
                canvas.drawPath(path, paint);
            }
            canvas.rotate(slotDegree);
        }
    }


    /**
     * 这里可以自定义你的元素路径
     */
    private void initPath() {
        path = new Path();
        path2 = new Path();
        radius = width / 2 - ringWidth - padding;
        float rectHalfWidth = ringElementHeight / 2;

        //圆角矩形
        RectF rectF = new RectF(-rectHalfWidth, radius, rectHalfWidth, radius + ringWidth);
        RectF rectF2 = new RectF(-rectHalfWidth, radius, rectHalfWidth, radius + emphasisWidth);

        //二阶贝塞尔曲线
//        path.moveTo(0, radius - 10);
//        path.lineTo(-rectHalfWidth, radius);
//        path.quadTo(0, radius + ringWidth - 20, rectHalfWidth, radius);
//        path.lineTo(0, radius - 10);
//        path.close();
//
//        path2.moveTo(0, radius - 10);
//        path2.lineTo(-rectHalfWidth, radius);
//        path2.quadTo(0, radius + emphasisWidth - 20, rectHalfWidth, radius);
//        path2.lineTo(0, radius - 10);
//        path2.close();

        //矩形
//        path.addRect(-1 * ringElementHeight / 2, radius, ringElementHeight / 2, width / 2 - padding, Path.Direction.CCW)
//       path2.addRect(-1 * ringElementHeight / 2, radius, ringElementHeight / 2, width / 2 - padding + emphasisWidth - ringWidth, Path.Direction.CCW);


        path.addRoundRect(rectF, 20, 20, Path.Direction.CCW);
        path2.addRoundRect(rectF2, 20, 20, Path.Direction.CCW);
    }


    //设置动画
    private void animation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, progress);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(animDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animProgress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }


    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public DashBoardView setProgress(float progress, boolean isAnimation) {
        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("progress out of the bounds, please set 0 < progress < 1");
        }
        animProgress = 0;
        this.progress = progress;
        if (isAnimation) {
            animation();
        } else {
            animProgress = progress;
            postInvalidate();
        }
        return this;
    }


    /**
     * 设置角度
     *
     * @param startDegree 开始角度
     * @param endDegree   终止角度
     * @param slotDegree  单位旋转角度
     */
    public DashBoardView setDegree(int startDegree, int endDegree, int slotDegree) {
        this.startDegree = startDegree;
        this.endDegree = endDegree;
        this.slotDegree = slotDegree;
        degreeCheck();
        postInvalidate();
        return this;
    }


    /**
     * 设置是否平衡自适应
     */
    public DashBoardView setBanAdaptive(boolean banAdaptive) {
        this.banAdaptive = banAdaptive;
        postInvalidate();
        return this;
    }


    /**
     * 设置阶梯数据集
     *
     * @param ladderValue 数据集
     */
    public DashBoardView setLadderValue(String[] ladderValue) {
        this.ladderValue = ladderValue;
        initData();
        postInvalidate();
        return this;
    }


    /**
     * 设置圆环背景色
     *
     * @param backgroundColor 圆环颜色
     */
    public DashBoardView setBackColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backPaint.setColor(backgroundColor);
        postInvalidate();
        return this;
    }


    /**
     * 设置进度颜色
     *
     * @param foregroundColor 进度颜色
     */
    public DashBoardView setForeColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        forePaint.setColor(foregroundColor);
        postInvalidate();
        return this;
    }


    /**
     * 设置圆环宽度
     *
     * @param ringWidth 圆环宽度
     */
    public DashBoardView setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
        postInvalidate();
        return this;
    }


    /**
     * 设置单位块高度（沿圆环方向的宽度）
     *
     * @param ringElementHeight
     */
    public DashBoardView setRingElementHeight(float ringElementHeight) {
        this.ringElementHeight = ringElementHeight;
        postInvalidate();
        return this;
    }


    /**
     * 设置强调单位块的宽度
     *
     * @param emphasisWidth
     */
    public DashBoardView setEmphasisWidth(float emphasisWidth) {
        this.emphasisWidth = emphasisWidth;
        postInvalidate();
        return this;
    }


    /**
     * 动画间隔
     *
     * @param animDuration
     */
    public DashBoardView setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
        postInvalidate();
        return this;
    }


    /**
     * 动画插值器
     *
     * @param interpolator
     */
    public DashBoardView setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        postInvalidate();
        return this;
    }


    public int getStartDegree() {
        return startDegree;
    }

    public int getEndDegree() {
        return endDegree;
    }

    public int getSlotDegree() {
        return slotDegree;
    }

    public String[] getLadderValue() {
        return ladderValue;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public float getRingWidth() {
        return ringWidth;
    }

    public float getRingElementHeight() {
        return ringElementHeight;
    }

    public float getEmphasisWidth() {
        return emphasisWidth;
    }

    public float getProgress() {
        return progress;
    }

    public float getPadding() {
        return padding;
    }

    public boolean isBanAdaptive() {
        return banAdaptive;
    }

    public long getAnimDuration() {
        return animDuration;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    /**
     * 合法性检查
     */
    private void degreeCheck() {
        if (slotDegree <= 0 || startDegree < 0 || endDegree < 0
                || slotDegree > 360 || startDegree > 360 || endDegree > 360
        ) {
            throw new IllegalArgumentException("data is illegal！");
        }
        if ((endDegree - startDegree) < slotDegree) {
            throw new IllegalArgumentException("angle can't be divided into slotDegree ");
        }
        if (((endDegree - startDegree) / slotDegree) < ladderNum) {
            throw new IllegalArgumentException("angle can't be divided into ladder num ");
        }
    }
}
