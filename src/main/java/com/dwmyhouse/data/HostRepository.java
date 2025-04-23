package com.dwmyhouse.data;

import com.dwmyhouse.models.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
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
     * Skips header and corrupt entries
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

    /**
     *Parses a single line of CSV into a Host object
     * Returns null if parsing fails or invalid
     */
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
        try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
            writer.println(HEADER);
            for (Host h : hosts) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        h.getId(),
                        h.getLastName(),
                        h.getEmail(),
                        h.getPhone(),
                        h.getAddress(),
                        h.getCity(),
                        h.getState(),
                        h.getPostalCode(),
                        h.getStandardRate().toPlainString(),
                        h.getWeekendsRate().toPlainString()
                );
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error writing hosts.csv: " + e.getMessage());
        }
        return false;
    }
}
