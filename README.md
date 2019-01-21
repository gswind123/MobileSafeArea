# MobileSafeArea  
## Brief  
这个项目的设计目标是：为跨平台移动游戏开发（例如Unity）提供SafeArea（避开Notch屏和圆角）
iOS平台会使用官方推荐的SafeArea方案，Android平台将尽量向其靠拢    
## Android  
通用的解决方案基于一个约定：<b>如果手机是Notch屏幕(有刘海)，那么认为其是圆角设计，而圆角设计统一加固定大小的内缩进</b>  
Android方案是基于目前中国主流机型的设计趋势，可能会在短时间内改变，届时再做出调整   