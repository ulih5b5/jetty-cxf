package name.hessenberger;

import javax.jws.WebService;

/**
 * Created by Uli Hessenberger on 07.02.17.
 */
@WebService(endpointInterface = "name.hessenberger.Hello", serviceName = "Hello")
public class HelloImpl implements Hello {

    @Override
    public String sayHi(String text) {
        System.out.println("sayHi called");
        return "Hello " + text;
    }
}
