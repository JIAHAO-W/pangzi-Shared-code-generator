package ${basePackage}.cli.command;


import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 作用是输出允许用户传入的动态参数信息
 */
@CommandLine.Command(name = "config",description = "查看参数信息",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{
    @Override
    public void run() {
        //实现config命令的逻辑
        System.out.println("查看参数信息");

        //利用Java 的反射机制，在程序运行时动态打印出对象属性的信息

        //方法一：利用JDK原生反射语法
        //Class<?> myclass = MainTemplateConfig.class;
        //获取类的所有字段
        //Field[] fields = myclass.getDeclaredFields();

        //方法二：利用Hutool的反射类工具
        Field[] fields = ReflectUtil.getFields(DataModel.class);

        //遍历并打印每个字段的信息
        for (Field field : fields) {
            System.out.println("字段名称："+ field.getName());
            System.out.println("字段类型："+ field.getType());
            System.out.println("------------------");
        }
    }
}
