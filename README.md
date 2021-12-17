# apt demo

- apt知识点（java5的时候已经存在，但是java6开始的时候才有可用的API）
  - 复习注解
  - 注解处理器(apt)的简单使用：APT的核心是AbstractProcessor类
  - 注解处理器(apt) 实现ButterKnife

- apt原理探索
  - 策略模式：一个类的行为或其算法可以在运行时更改。
  - SPI(Service Provider Interface)服务发现机制: 通过读取文本中的类全名，反射生成接口的实现类，再执行实现类中的方法。是一种策略模式+IOC注入的综合体。
  - 注解处理器(apt)的执行原理：是基于SPI(Service Provider Interface)服务发现机制实现的

- asm字节码插桩框架
  - asm字节码插桩框架：asm用于修改class文件
  - 转逆波兰表达式：学习中缀表达式转成后缀表达式、了解为什么计算机要使用后缀表达式
