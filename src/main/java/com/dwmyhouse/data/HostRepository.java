package com.dwmyhouse.data;

import com.dwmyhouse.models.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for reading Host data from hosts.csv
 */
@Repository
public class HostRepository {

    private static final String HEADER = "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate";
    private final Path filePath;

    public HostRepository(@Value("${host.file.path:./data/hosts.csv}") String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads all Hosts from the hosts.csv file
     * @return list of all Hosts
     */
    public List<Host> findAll() {
        List<Host> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst && line.equalsIgnoreCase(HEADER)) {
                    isFirst = false;
                    continue;
                }
                Host host = deserialize(line);
                if (host != null) {
                    result.add(host);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading hosts.csv: " + e.getMessage());
        }
        return result;
    }

    /**
     * Finds a host by their email address
     * @param email the host's email
     * @return host or null if not found
     */
    public Host findByEmail(String email) {
        return findAll().stream()
                .filter(h -> h.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    private Host deserialize(String line) {
        String[] tokens = line.split(",", -1);
        if (tokens.length != 10) return null;

        Host host = new Host();
        host.setId(tokens[0]);
        host.setLastName(tokens[1]);
        host.setEmail(tokens[2]);
        host.setPhone(tokens[3]);
        host.setAddress(tokens[4]);
        host.setCity(tokens[5]);
        host.setState(tokens[6]);
        host.setPostalCode(tokens[7]);
        try{
            host.setStandardRate(new BigDecimal(tokens[8]));
            host.setWeekendsRate(new BigDecimal(tokens[9]));
        } catch (NumberFormatException e) {
            return null; //skips invalid number format
        }

        return host;
    }
}
