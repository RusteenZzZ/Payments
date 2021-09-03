package SuperDuperMegaProject.Rustam.Exception;

public class FeignClientException_5xx extends RuntimeException{
    public FeignClientException_5xx(String msg){
        super("5xx : " + msg);
    }
}
