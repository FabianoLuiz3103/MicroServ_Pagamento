package exception;

public class ResourceNotFoundException extends RuntimeException{

    //RuntimeException não precisa de try-catch
    //Exception customizada para quando não encontrar o recurso

    public ResourceNotFoundException(String message){
        super(message);
    }
}
