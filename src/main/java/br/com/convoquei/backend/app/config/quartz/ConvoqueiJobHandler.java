package br.com.convoquei.backend.app.config.quartz;

@FunctionalInterface
public interface ConvoqueiJobHandler {

    void handle() throws Exception;

}