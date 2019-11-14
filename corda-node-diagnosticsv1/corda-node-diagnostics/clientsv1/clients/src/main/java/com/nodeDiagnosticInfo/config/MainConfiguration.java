package com.nodeDiagnosticInfo.config;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.nodeDiagnosticInfo.NodeDiagnosticInfoPublicationService;
import com.nodeDiagnosticInfo.events.PublicationNotifier;
import com.nodeDiagnosticInfo.monitoring.cloudwatch.CloudwatchMetricsEmitter;
import com.nodeDiagnosticInfo.repository.NodeDiagnosticInfoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@Import(EnvironmentConfiguration.class)
public class MainConfiguration {

    private static final String STAGE_PROPERTY_NAME = "stage";
    private static final String REGION_PROPERTY_NAME = "region";

    private static final String PUBLISHED_NODE_DIAGNOSTIC_INFO_QUEUE = "published_node_diagnostic_info_queue";

    @Autowired
    private ConfigurableEnvironment environment;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        AmazonDynamoDB dynamoDB = getDynamoDB();

        return new DynamoDBMapper(dynamoDB);
    }

    @Bean
    public NodeDiagnosticInfoRepository nodeDiagnosticInfoRepository(final DynamoDBMapper dynamoDBMapper) {
        return new NodeDiagnosticInfoRepository(dynamoDBMapper);
    }

    @Bean
    public NodeDiagnosticInfoPublicationService nodeDiagnosticInfoPublicationService(final PublicationNotifier publicationNotifier,
                                                         final NodeDiagnosticInfoRepository nodeDiagnosticInfoRepository) {
        return new NodeDiagnosticInfoPublicationService(nodeDiagnosticInfoRepository, publicationNotifier);
    }

    @Bean
    public PublicationNotifier publicationNotifier() {
        final String region = environment.getProperty(REGION_PROPERTY_NAME);

        return new PublicationNotifier(
                AmazonSQSClientBuilder.standard().
                        withRegion(region).build(),
                        PUBLISHED_NODE_DIAGNOSTIC_INFO_QUEUE
        );
    }

    @Bean
    public CloudwatchMetricsEmitter metricsEmitter() {
        final String region = environment.getProperty(REGION_PROPERTY_NAME);
        final String stage = environment.getProperty(STAGE_PROPERTY_NAME);

        AmazonCloudWatch cloudwatchClient = AmazonCloudWatchClientBuilder.standard()
                .withRegion(region)
                .build();

        return new CloudwatchMetricsEmitter(cloudwatchClient, stage);
    }

    private AmazonDynamoDB getDynamoDB() {
        final String stage = environment.getProperty(STAGE_PROPERTY_NAME);
        final String region = environment.getProperty(REGION_PROPERTY_NAME);

        switch (stage) {
            case "dev":
                return AmazonDynamoDBClientBuilder.standard()
                        .withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", region))
                        .build();
            case "prod":
                return AmazonDynamoDBClientBuilder.standard()
                        .withRegion(region)
                        .build();
            default:
                throw new RuntimeException("Stage defined in properties unknown: " + stage);
        }
    }
}
