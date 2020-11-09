package no.kristiania.database;

public class HttpErrorMessage {
    int errorCode;
    String errorMessage;
    String requestPath;
    String info;

    public HttpErrorMessage( String requestPath, int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.requestPath = requestPath;
    }

    public HttpErrorMessage(int errorCode, String errorMessage, String info) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.info = info;
    }

    public String getErrorMessage(){
        return "<h1 style='margin-top: 5vh;'>" + errorCode + " " + errorMessage + "<h1><h3> <br>We couldn't find this page.</h3><h4>" +
                requestPath + " does not exist</h4>" +
                "<a href=/index.html>Go to front page</a>";
    }

    public String getInfoMessage(){
        return "<h1 style='margin-top: 5vh;'>" + errorCode + " " + errorMessage + "</h1> <h3>" +
                info + "</h3>" +
                "<a href=/index.html>Go to front page</a>";
    }
}
