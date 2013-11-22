package io.ei.jsontoxls;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import io.ei.jsontoxls.health.JsonToXLSHealthCheck;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.resources.TemplateResource;
import io.ei.jsontoxls.resources.XlsResource;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;
import org.skife.jdbi.v2.DBI;

public class JsonToXlsService extends Service<JsonToXlsConfiguration> {
    public static void main(String[] args) throws Exception {
        new JsonToXlsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<JsonToXlsConfiguration> bootstrap) {
        bootstrap.setName("json-to-excel");
    }

    @Override
    public void run(JsonToXlsConfiguration configuration, Environment environment) throws Exception {
        String xlsTemplate = configuration.getXlsTemplate();
        String packageName = "io.ei.jsontoxls.domain";
        String outputDirectory = "output";
        String className = "Data";
        JsonPojoConverter converter = new JsonPojoConverter(packageName, className, outputDirectory);
        ObjectDeserializer objectDeserializer = new ObjectDeserializer(outputDirectory, className);
        PackageUtils packageUtil = new PackageUtils(outputDirectory);
        ExcelUtils excelUtil = new ExcelUtils();
        DBIFactory factory = new DBIFactory();
        DBI dbInterface = factory.build(environment, configuration.getDatabaseConfiguration(), "json_to_xls");
        TemplateRepository templateRepository = dbInterface.onDemand(TemplateRepository.class);
        XlsResource resource = new XlsResource(converter, objectDeserializer, packageUtil, excelUtil, xlsTemplate,
                templateRepository);
        environment.addResource(resource);
        environment.addHealthCheck(new JsonToXLSHealthCheck(resource));

        environment.addResource(new TemplateResource(templateRepository));
    }
}
