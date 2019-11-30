# DashBoardView

**一个简单的仪表盘儿。**<br><br>
做一个**与众不同**的轮子。
> - DashBoardView是一个仪表盘视图，支持自定义颜色，文本，刻度尺寸，动画等多种属性
> - 起始角度自适应，分段不能整除角度范围自适应
> - 一个轮子实现不同效果
> - 动画可配置插值器。


<a name="nBi60"></a>
#### 新鲜出炉
新鲜出炉的自定义View，可能存在较多bug，

<a name="aEmll"></a>
#### 简单使用

```xml
<cn.surine.dashboardview.DashBoardView
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       android:id="@+id/dash"
       android:layout_marginTop="50dp"
       app:foregroundColor="#EF5350"
       app:backgroundColor="#7C26A69A"
       />
```

<a name="8cygs"></a>
#### 示意图
(仅圆环和圆环文字部分，按钮和进度条是其他内容)
![image.png](https://cdn.nlark.com/yuque/0/2019/png/276442/1575084307156-fcb361e5-447a-4b59-8c21-ee68b201e48f.png#align=left&display=inline&height=367&name=image.png&originHeight=996&originWidth=610&size=54164&status=done&style=none&width=225)![image.png](https://cdn.nlark.com/yuque/0/2019/png/276442/1575091470542-852f55ac-6b5b-4a62-83b3-659660561935.png#align=left&display=inline&height=366&name=image.png&originHeight=990&originWidth=616&size=50459&status=done&style=none&width=228)![image.png](https://cdn.nlark.com/yuque/0/2019/png/276442/1575092819838-223d85a9-48d7-4e17-9719-adbd6191ee72.png#align=left&display=inline&height=364&name=image.png&originHeight=982&originWidth=612&size=69324&status=done&style=none&width=227)

<a name="uAMeJ"></a>
#### API
| API | 功能 |
| --- | --- |
| setProgress(float progress, boolean isAnimation) | 设置当前进度（进度值，是否开启动画true为开启） |
| setDegree(int startDegree, int endDegree,int slotDegree) | 设置起始和终止角度和间隔角度，竖直向下为0度，顺时针增大,最小0度，最大360度，间隔角度默认为4，推荐2-4,不允许为0，若要实现连续效果请调高ElementHeight |
| setBanAdaptive(boolean banAdaptive) | 是否禁止自平衡，默认开启，自平衡开启时，设置不合理的度数和 度数间隔不能等分为要求的段数时会处理多余部分并进行适当的旋转 |
| setLadderValue(String[] ladderValue)  | 设置数据集 |
| setBackColor(int backgroundColor) | 设置底层环颜色 |
| setForeColor(int foregroundColor) | 设置进度环颜色 |
| setRingWidth(float ringWidth) | 设置圆环宽度 |
| setRingElementHeight(float ringElementHeight) | 设置小线段的宽度（圆环径向，垂直于圆环宽度方向） |
| setEmphasisWidth(float emphasisWidth) | 设置强调线段长度，类似于圆环宽度，圆环宽度是非强调线段长度 |
| setAnimDuration(long animDuration) | 设置动画时长 |
| setInterpolator(Interpolator interpolator) | 设置动画插值器 |


自定义控件中对应的属性也可配置。

<a name="ay3aI"></a>
#### 注意

1. 建议先配置数据集，然后在设置角度，如果不按照此顺序，可能出现不可避免的问题。
1. 默认情况下开启旋转自适应，会由程序自动处理错误和不合理的数据设置，如果需要禁止自适应则可调用相应方法来关闭。
1. 本View是个仪表盘View，不是个简单的圆环的等分，所以当设置0-360度等分4份时会出现第1份和第4份重合，设计如此，请见谅。


<a name="2LE2q"></a>
#### 更新计划
目前暂时没有依赖形式或者抽单独的moudle，排查完错误之后会单独分离moudle和发Jcenter，喜欢的童鞋目前可以Copy或者参考。

> - 自定义元素路径，可支持多种曲线路径
> - 表盘指针等元素


