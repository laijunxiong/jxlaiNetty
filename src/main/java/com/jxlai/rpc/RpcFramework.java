package com.jxlai.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc框架
 * @author lizhi
 *
 */
public class RpcFramework {

	private static Logger logger = LoggerFactory.getLogger(RpcFramework.class);

	/**
	 * 暴露服务
	 * @param service
	 * @param port
	 * @throws Exception
	 */
	public static void export(final Object service, int port) throws Exception {
		if (null == service)
			throw new IllegalArgumentException("service arguement is null");

		if (port <= 0 || port > 65535)
			throw new IllegalArgumentException("port is illegal");

		logger.info("{} service start on port {}", service.getClass()
				.getSimpleName(), port);

		ServerSocket server = new ServerSocket(port);

		for(;;) {  
            try {  
                final Socket socket = server.accept();  
                new Thread(new Runnable() {  
                    @Override  
                    public void run() {  
                        try {  
                            try {  
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());  
                                try {  
                                    String methodName = input.readUTF();  
                                    Class<?>[] parameterTypes = (Class<?>[])input.readObject();  
                                    Object[] arguments = (Object[])input.readObject();  
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());  
                                    try {  
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);  
                                        Object result = method.invoke(service, arguments);  
                                        output.writeObject(result);  
                                    } catch (Throwable t) {  
                                        output.writeObject(t);  
                                    } finally {  
                                        output.close();  
                                    }  
                                } finally {  
                                    input.close();  
                                }  
                            } finally {  
                                socket.close();  
                            }  
                        } catch (Exception e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }).start();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
	/**
	 * 引用服务
	 * @param interfaceClass
	 * @param host
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) throws Exception {  
        if (interfaceClass == null)  
            throw new IllegalArgumentException("Interface class == null");  
        if (! interfaceClass.isInterface())  
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");  
        if (host == null || host.length() == 0)  
            throw new IllegalArgumentException("Host == null!");  
        if (port <= 0 || port > 65535)  
            throw new IllegalArgumentException("Invalid port " + port);  
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);  
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new InvocationHandler() {  
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {  
                Socket socket = new Socket(host, port);  
                try {  
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());  
                    try {  
                        output.writeUTF(method.getName());  
                        output.writeObject(method.getParameterTypes());  
                        output.writeObject(arguments);  
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());  
                        try {  
                            Object result = input.readObject();  
                            if (result instanceof Throwable) {  
                                throw (Throwable) result;  
                            }  
                            return result;  
                        } finally {  
                            input.close();  
                        }  
                    } finally {  
                        output.close();  
                    }  
                } finally {  
                    socket.close();  
                }  
            }  
        });  
    }  
	
	
}
