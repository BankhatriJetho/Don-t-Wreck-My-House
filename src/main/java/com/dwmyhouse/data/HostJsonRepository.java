package com.dwmyhouse.data;

import com.dwmyhouse.models.Host;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HostJsonRepository {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public HostJsonRepository(@Value("${host.json.path:./data/hosts.json}") String filePath) {
        this.file = new File(filePath);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<Host> findAll() {
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return mapper.readValue(file, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println("Error reading hosts.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Host findByEmail(String email) {
        return findAll().stream()
                .filter(h -> h.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public boolean add(Host host) {
        List<Host> hosts = findAll();
        hosts.add(host);
        return writeAll(hosts);
    }

    public boolean update(Host updatedHost) {
        List<Host> hosts = findAll();
        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getId().equals(updatedHost.getId())) {
                hosts.set(i, updatedHost);
                return writeAll(hosts);
            }
        }
        return false;
    }

    public boolean delete(String hostId) {
        List<Host> hosts = findAll();
        boolean removed = hosts.removeIf(h -> h.getId().equals(hostId));
        return removed && writeAll(hosts);
    }

    private boolean writeAll(List<Host> hosts) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, hosts);
            return true;
        } catch (Exception e) {
            System.out.println("Error writing hosts.json: " + e.getMessage());
            return false;
        }
    }

}
