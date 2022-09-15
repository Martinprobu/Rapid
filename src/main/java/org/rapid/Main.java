package org.rapid;

import org.rapid.common.enums.DomainType;
import org.rapid.core.IMain;
import org.rapid.core.service.IExecService;
import org.rapid.core.service.V1Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * main entrance
 */
@Configuration
@ComponentScan("org.rapid.core.service")
public class Main implements IMain {
    public Main(IExecService execService) {
        this.execService = execService;
    }

    static Logger log = Logger.getGlobal();
    private IExecService execService;

    @Override
    public void exec() {
        Arrays.stream(DomainType.values()).forEach((type) -> {
            try {
                exec(type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void exec(DomainType type) throws IOException {
        execService.handle(type);
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {

        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        V1Service service = (V1Service)context.getBean(V1Service.class);
        log.info(service.toString());

        // run the java domain @Deprecated
//        new Main(service).exec(DomainType.JAVA);

        // template  @TemplateDomainDefaultHandler
        new Main(service).exec(DomainType.TEMPLATE);

//        run all domains, rarely used
//        new Main(service).exec();

    }
}
