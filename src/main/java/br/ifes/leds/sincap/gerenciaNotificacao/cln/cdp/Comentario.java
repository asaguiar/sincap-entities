package br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp;

import br.ifes.leds.reuse.persistence.ObjetoPersistente;
import br.ifes.leds.sincap.controleInterno.cln.cdp.Funcionario;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;

/**
 * Created by aleao on 24/03/2015.
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@Entity
public class Comentario extends ObjetoPersistente  {

    @OneToOne
    private Funcionario funcionario;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dataComentario;

    @Column
    private String descricao;
}
