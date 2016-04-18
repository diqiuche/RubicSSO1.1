package com.rubic.sso.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * Created by LiuMian on 2016/2/28.
 */
public class SqlSessionPool {

    private SqlSessionFactory sessionFactory;

    private static SqlSessionPool sqlSessionPool = null;

    private SqlSessionPool() {
    	init();
    }

    private void init() {

        //加载myBatis配置文件
        InputStream is = SqlSessionPool.class.getClassLoader().getResourceAsStream("conf.xml");

        //从配置文件中构建�?��SqlSessionFactory
        sessionFactory = new SqlSessionFactoryBuilder().build(is);


        //通过session获得�?��mapper
//        UserMapper mapper = session.getMapper(UserMapper.class);
//
//        //通过mapper可以直接调用接口中的方法，并且不�?��强制转换！！
//        //这是�?��与不绑定namespace配置的一个非常大的区别和优势�?
//        User u1 = mapper.selectUserById(1);
//        User u2 = mapper.selectUserById(3);
//
//        System.out.println(u1.toString());
//        System.out.println(u2.toString());

    }

    public static SqlSessionPool getSqlSessionPool(){
        if (sqlSessionPool == null ){
            sqlSessionPool = new SqlSessionPool();
        }
        return sqlSessionPool;
    }

    public SqlSession newSqlSession(){
        return sessionFactory.openSession();
    }


}
