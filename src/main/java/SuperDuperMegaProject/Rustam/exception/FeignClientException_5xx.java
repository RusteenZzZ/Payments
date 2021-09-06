package SuperDuperMegaProject.Rustam.exception;

public class FeignClientException_5xx extends RuntimeException{
    public FeignClientException_5xx(String msg){
        super("5xx : " + msg);
    }
}
