package cn.bugstack.config;


import cn.bugstack.types.annotation.DCCValue;
import cn.bugstack.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DCCValueBeanFactory implements BeanPostProcessor {

    private static final String BASE_CONFIG_PATH = "group_buy_market_dcc_";

    private final RedissonClient redissonClient;

    private final Map<String, Object> dccObjGroup = new HashMap<>();

    public DCCValueBeanFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    //注册dcc监听器
    @Bean("dccTopic")
    public RTopic dccRedisTopicListener(RedissonClient redissonClient){
        //获取管理dcc的控制器
        RTopic topic = redissonClient.getTopic("group_buy_market_dcc");
        //监听dcc
        topic.addListener(String.class, ((charSequence, s) -> {
            String[] split = s.split(Constants.SPLIT);

            //将接受的消息进行解析，获取key和value
            String attribute = split[0];
            String key = BASE_CONFIG_PATH + attribute;
            String value = split[1];

            //判断redis中是否存在key，如果不存在则停止更新；如果存在key，则更新redis中的值
            RBucket<String> bucket = redissonClient.getBucket(key);
            boolean exists = bucket.isExists();
            if(!exists) return;
            bucket.set(value);

            //获取对应的bean对象
            Object objBean = dccObjGroup.get(key);
            if(null == objBean) return;

            //获取bean对象的真实class对象
            Class<?> objBeanClass = objBean.getClass();
            if(AopUtils.isAopProxy(objBean)){
                objBeanClass = AopUtils.getTargetClass(objBean);
            }

            try {
                //获取objBeanClass中的attribute字段，并设置值
                Field field = objBeanClass.getDeclaredField(attribute);
                field.setAccessible(true);
                field.set(objBean, value);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }));
        return topic;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        //获取当前的bean对象和class对象
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;

        //将获取的代理对象去除包装，获取真实的bean对象和class对象
        if(AopUtils.isAopProxy(targetBeanObject)){
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        //从targetBeanClass中获取所有被@DCCValue注解修饰的字段
        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields){
            //判断字段是否被@DCCValue注解修饰，如果没有被注解修饰则跳过该字段
            if(!field.isAnnotationPresent(DCCValue.class)){
                continue;
            }
            //获取@DCCValue注解的value值，如果value值为空则抛出异常
            DCCValue dccValue = field.getAnnotation(DCCValue.class);
            String value = dccValue.value();
            if(StringUtils.isBlank(value)){
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }

            //获取key和值，按照:分割，如果没有值，则默认值为null
            String[] splits = value.split(":");
            //拼接key
            String key = BASE_CONFIG_PATH.concat(splits[0]);
            //如果split长度为2，则默认值为split[1]，否则默认值为null
            String defaultValue = splits.length == 2 ? splits[1] : null;

            String setValue = defaultValue;

            try {
                //判断默认值是否为空，如果为空，则抛出异常
                if(StringUtils.isBlank(defaultValue)){
                    throw new RuntimeException("dcc config error " + key + " is not null - 请配置默认值！");
                }

                //将key作为访问redis的钥匙
                RBucket<String> bucket = redissonClient.getBucket(key);
                //判断redis里是否存在key，如果不存在说明是第一次运行，将默认值写入redis；如果redis中存在key，则从redis中获取值
                boolean exists = bucket.isExists();
                if(!exists){
                    bucket.set(setValue);
                } else{
                    setValue = bucket.get();
                }

                //开锁，将redis中获取的配置值设置到字段中
                field.setAccessible(true);
                field.set(targetBeanObject, setValue);
                //关锁
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            //将当前的key和bean对象存储到Map中，以便下次热更新时能快速找到bean对象
            dccObjGroup.put(key, targetBeanObject);
        }

        return bean;
    }

}
