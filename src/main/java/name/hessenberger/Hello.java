package name.hessenberger;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by Uli Hessenberger on 07.02.17.
 */
@WebService
public interface Hello {

    String sayHi(@WebParam(name="text") String text);
}
