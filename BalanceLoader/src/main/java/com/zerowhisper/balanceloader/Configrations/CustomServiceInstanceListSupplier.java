package com.zerowhisper.balanceloader.Configrations;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomServiceInstanceListSupplier implements ServiceInstanceListSupplier {

    private final String serviceId="localhost-ShortURL";
    @Override
    public  String getServiceId() {
        return serviceId;
    }
    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(Arrays.asList(
                new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8081, false),
                new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8082, false),
                new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 8083, false)
        ));
    }

}
