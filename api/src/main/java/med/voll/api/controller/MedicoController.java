package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired //o spring que vai instanciar o repository
    private MedicoRepository medicoRepository;

    @PostMapping
    @Transactional //garantir uma transação ativa com o banco de dados.
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {

        var medico = new Medico(dados);

        medicoRepository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}")
                .buildAndExpand(medico.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));

    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(Pageable paginacao) {

        var page = medicoRepository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);

        return ResponseEntity.ok(page);

    }//Fazendo uma query de findAll com paginação, devemos passar como parametro do metodo o Pageable e repassar no met-
    //odo findAll a referencia. Em seguida precisamos fazer um map conforme apresentado.

    //TIPO DA REQUISICAO: http://localhost:8080/medicos?size=1&page=1&sort=nome << OBSERVE OS PARÂMETROS
    //O PARAMETRO SORT SERVE PARA DEFINIR QUAL ATRIBUTO IRA DEFINIR A ORDEM (CRESCENTE) ou sort=nome,desc (DESCRESCENTE)

    //Também podemos setar alguns padrões com a anotação @PageableDefault(size..., sort...,) dentre outros parâmetros

   /* @GetMapping
    public List<DadosListagemMedico> listar() {
        return medicoRepository.findAll()
                .stream()
                .map(DadosListagemMedico::new)
                .toList();
    }Convertendo um objeto completo em um objeto reduzido somente com as informações necessárias na listagem
    Essa conversão foi feita utilizando o stream, map e transformando em lista. Para isso é necessário um DTO,
    com um construtor inicializando todos os parâmetros recebidos no record. */

    @GetMapping("/{id}")
    public ResponseEntity detalharMedico(@PathVariable Long id){

        var medico = medicoRepository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));

    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados){

        var medico = medicoRepository.getReferenceById(dados.id());

        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));

    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id){

        var medico = medicoRepository.getReferenceById(id);

        medico.excluiLogico();

        return ResponseEntity.noContent().build();

    }

}
