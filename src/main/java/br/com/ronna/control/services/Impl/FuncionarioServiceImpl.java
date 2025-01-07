package br.com.ronna.control.services.Impl;

import br.com.ronna.control.models.FuncionarioModel;
import br.com.ronna.control.repositories.FuncionarioRepository;
import br.com.ronna.control.services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FuncionarioServiceImpl implements FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    public List<FuncionarioModel> findAll() {
        return funcionarioRepository.findAll();
    }

    @Override
    public Optional<FuncionarioModel> findById(UUID funcionarioId) {
        return funcionarioRepository.findById(funcionarioId);
    }

    @Override
    public boolean existsByFuncionarioCPF(String funcionarioCPF) {
        return funcionarioRepository.existsByFuncionarioCPF(funcionarioCPF);
    }

    @Override
    public void save(FuncionarioModel funcionarioModel) {
        funcionarioRepository.save(funcionarioModel);
    }

    @Override
    public void delete(FuncionarioModel funcionarioModel) {
        funcionarioRepository.delete(funcionarioModel);
    }

    @Override
    public Page<FuncionarioModel> findAll(Pageable pageable) {
        return funcionarioRepository.findAll(pageable);
    }

    @Override
    public Page<FuncionarioModel> findAllAtivos(Pageable pageable) {
        return funcionarioRepository.findAllByFuncionarioStatus(pageable);
    }
}
