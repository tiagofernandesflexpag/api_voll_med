package med.voll.api.medico;

import jakarta.validation.constraints.NotNull;
import med.voll.api.endereco.DadosEndereco;
import med.voll.api.endereco.Endereco;

public record DadosAtualizacaoMedico(
        @NotNull
        Long id,
        String telefone,
        String email,
        DadosEndereco endereco) {}