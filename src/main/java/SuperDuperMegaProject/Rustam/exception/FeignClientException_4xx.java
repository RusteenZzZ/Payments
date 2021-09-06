package SuperDuperMegaProject.Rustam.exception;

public class FeignClientException_4xx extends RuntimeException{
    public FeignClientException_4xx(String msg){
        super("4xx : " + msg);
    }
}
