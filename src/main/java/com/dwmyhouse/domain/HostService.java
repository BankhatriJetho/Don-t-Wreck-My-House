package com.dwmyhouse.domain;

import com.dwmyhouse.data.HostRepository;
import com.dwmyhouse.models.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides host-related business operations
 */
@Service
public class HostService {

    private final HostRepository repository;

    @Autowired
    public HostService(HostRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds a host by email
     * @param email the host's email
     * @return Host or null if not found
     **/
    public Host getHostByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * For stretch goal-- unused now
     * Returns all hosts
     * @return List of hosts
     */
    public List<Host> findAll() {
        return repository.findAll();
    }

    public boolean addHost(Host host) {
        return repository.add(host);
    }

    public boolean updateHost(Host host) {
        return repository.update(host);
    }

    public boolean deleteHost(String hostId) {
        return repository.delete(hostId);
    }
}
