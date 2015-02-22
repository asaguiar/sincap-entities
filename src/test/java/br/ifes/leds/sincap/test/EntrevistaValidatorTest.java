package br.ifes.leds.sincap.test;

import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.*;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.dto.*;
import br.ifes.leds.sincap.validacao.groups.entrevista.EntrevistaNaoRealizada;
import br.ifes.leds.sincap.validacao.groups.entrevista.EntrevistaRealizadaDoacaoNaoAutorizada;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import static br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.TipoNaoDoacao.PROBLEMAS_ESTRUTURAIS;
import static br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.TipoNaoDoacao.RECUSA_FAMILIAR;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Locale.getDefault;
import static org.junit.Assert.*;

public class EntrevistaValidatorTest extends AbstractionTest {

    public static final boolean sim = true;
    public static final boolean nao = false;
    private static Validator validator;
    
    @Autowired
    private Mapper mapper;
    private ProcessoNotificacao processoNotificacao;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Before
    public void before() {
        this.processoNotificacao = processoComObitoValido();
    }
    
    @Test
    public void processoSemIdComEntrevistaValida() {
        processoNotificacao.setEntrevista(entrevistaNaoRealizada());
        processoNotificacao.setCausaNaoDoacao(new CausaNaoDoacao());
        processoNotificacao.getCausaNaoDoacao().setTipoNaoDoacao(PROBLEMAS_ESTRUTURAIS);

        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao, EntrevistaNaoRealizada.class);
        boolean processoComId = sim;

        for (ConstraintViolation<ProcessoNotificacao> violation : violations) {
            if (violation.getMessage().equals("Processo sem ID"))
                processoComId = nao;
        }

        assertFalse("Processo sem ID está sendo válido", processoComId);
    }

    @Test
    public void processoSemIdSemEntrevista() {
        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void processoComId() {
        processoNotificacao.setId(1L);
        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao);
        boolean processoComId = sim;

        for (ConstraintViolation<ProcessoNotificacao> violation : violations) {
            if (violation.getMessage().equals("Processo sem ID"))
                processoComId = nao;
        }

        assertTrue("Processo com ID está sendo inválido", processoComId);
    }
    
    @Test
    public void processoComEntrevistaNull() {
        processoNotificacao.setId(1L);
        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao);
        
        assertEquals(0, violations.size());
    }

    @Test
    public void entrevistaNaoRealizadaValida() {
        processoNotificacao.setId(1L);
        processoNotificacao.setEntrevista(entrevistaNaoRealizada());
        processoNotificacao.setCausaNaoDoacao(new CausaNaoDoacao());
        processoNotificacao.getCausaNaoDoacao().setTipoNaoDoacao(PROBLEMAS_ESTRUTURAIS);
        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao, EntrevistaNaoRealizada.class);

        assertEquals("Entrevista validando quando não deveria.", 0, violations.size());
    }

    @Test
    public void entrevistaNaoRealizadaSemCausaNaoDoacao() {
        processoNotificacao.setId(1L);
        processoNotificacao.setEntrevista(entrevistaNaoRealizada());
        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao, EntrevistaNaoRealizada.class);
        final boolean temErro = temErro(violations, "Causa da entrevista não ter sido realizada não informada");

        assertTrue("Causa de não doação não validando.", temErro);
    }

    @Test
    public void entrevistaNaoRealizadaComAMais() {
        processoNotificacao.setId(1L);
        processoNotificacao.setEntrevista(entrevistaNaoRealizada());
        processoNotificacao.getEntrevista().setDataEntrevista(hoje());
        processoNotificacao.getEntrevista().setResponsavel(new Responsavel());
        processoNotificacao.getEntrevista().setResponsavel2(new Responsavel());
        processoNotificacao.getEntrevista().setTestemunha1(new Testemunha());
        processoNotificacao.getEntrevista().setTestemunha2(new Testemunha());
        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao, EntrevistaNaoRealizada.class);
        final boolean temErro = temErro(violations, "Campo testemunha 1 está preenchido");

        assertTrue("Validação da entrevista não realizada com dados a mais está errada.", temErro);
    }

    @Test
    public void entrevistaRealizadaDoacaoNaoAutorizadaComDadosPaciente() {
        processoNotificacao.setId(1L);
        processoNotificacao.setCausaNaoDoacao(new CausaNaoDoacao());
        processoNotificacao.getCausaNaoDoacao().setTipoNaoDoacao(RECUSA_FAMILIAR);
        processoNotificacao.setEntrevista(entrevistaRealizada());
        processoNotificacao.getEntrevista().setDoacaoAutorizada(nao);

        final Set<ConstraintViolation<ProcessoNotificacao>> violations = validator.validate(processoNotificacao, EntrevistaRealizadaDoacaoNaoAutorizada.class);

        assertEquals(0, violations.size());
    }

    private Entrevista entrevistaRealizada() {
        final EntrevistaDTO entrevistaDTO = EntrevistaDTO.builder()
            .dataCadastro(hoje())
            .entrevistaRealizada(sim)
            .build();

        return mapper.map(entrevistaDTO, Entrevista.class);
    }

    private Entrevista entrevistaNaoRealizada() {
        EntrevistaDTO entrevistaDTO = EntrevistaDTO.builder()
            .dataCadastro(hoje())
            .entrevistaRealizada(nao)
        .build();

        return mapper.map(entrevistaDTO, Entrevista.class);
    }
    
    private ProcessoNotificacao processoComObitoValido() {
        final ProcessoNotificacaoDTO notificacaoDTO = ProcessoNotificacaoDTO.builder()
                .codigo("SOMECODE")
                .dataAbertura(hoje())
                .historico(new ArrayList<AtualizacaoEstadoDTO>())
                .notificador(1L)
                .obito(ObitoDTO.builder()
                        .dataObito(ontem())
                        .dataCadastro(hoje())
                        .aptoDoacao(sim)
                        .primeiraCausaMortis(new CausaMortis())
                        .segundaCausaMortis(new CausaMortis())
                        .paciente(new PacienteDTO())
                        .setor(1L)
                        .hospital(1L)
                        .build()).build();
        
        return mapper.map(notificacaoDTO, ProcessoNotificacao.class);
    }

    private static boolean temErro(Set<ConstraintViolation<ProcessoNotificacao>> violations, String msgErro) {
        boolean temErro = nao;
        for (ConstraintViolation<ProcessoNotificacao> violation : violations) {
            if (violation.getMessage().equals(msgErro))
                temErro = sim;
        }
        return temErro;
    }

    private static Calendar hoje() {
        return Calendar.getInstance(getDefault());
    }

    private static Calendar ontem() {
        final Calendar ontem = hoje();

        ontem.set(DAY_OF_MONTH, ontem.get(DAY_OF_MONTH) - 1);
        
        return ontem;
    }
}
