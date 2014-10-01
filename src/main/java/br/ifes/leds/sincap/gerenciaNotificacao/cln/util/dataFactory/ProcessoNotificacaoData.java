/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ifes.leds.sincap.gerenciaNotificacao.cln.util.dataFactory;

import br.ifes.leds.sincap.controleInterno.cgd.CaptadorRepository;
import br.ifes.leds.sincap.controleInterno.cgd.NotificadorRepository;
import br.ifes.leds.sincap.controleInterno.cln.cdp.Captador;
import br.ifes.leds.sincap.controleInterno.cln.cdp.Notificador;
import br.ifes.leds.sincap.gerenciaNotificacao.cgd.AtualizacaoEstadoRepository;
import br.ifes.leds.sincap.gerenciaNotificacao.cgd.ProcessoNotificacaoRepository;
import br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.*;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.ifes.leds.reuse.utility.Factory.criaObjeto;
import static br.ifes.leds.sincap.gerenciaNotificacao.cln.cdp.EstadoNotificacaoEnum.*;

/**Classe para a criação de objetos ProcessoNotificacao randomicos.
 *
 * @author aleao
 * @version 1.0
 */
@Service
public class ProcessoNotificacaoData {
    
    @Autowired
    private ProcessoNotificacaoRepository processoNotificacaoRepository;
    @Autowired
    private NotificadorRepository notificadorRepository;
    @Autowired
    private CaptadorRepository captadorRepository;
    @Autowired
    private AtualizacaoEstadoRepository atualizacaoEstadoRepository;
    @Autowired
    private ObitoData obitoData;
    @Autowired
    private EntrevistaData entrevistaData;
    @Autowired
    private CaptacaoData captacaoData;


    /**Método responsável por criar processo de notificação randomico até a
     * etapa de Analise de Óbito de forma randomica.
     * @param df - instancia DataFactory.
     * @param qtdAna - quantidade de processos.
     */
    @SuppressWarnings("unused")
    public void criarAnaliseObitoRandom(DataFactory df,Integer qtdAna){
        for (int i = 0; i < qtdAna;i++){
            ProcessoNotificacao pn = criarAnaliseObito(df);
            List<AtualizacaoEstado> listAtualizacao = new ArrayList<>();
            listAtualizacao.add(AtualizaEstadoNotificacao(pn,1));
            pn.setHistorico(listAtualizacao);
            
            for(AtualizacaoEstado ae : listAtualizacao){
                pn.setUltimoEstado(ae);
            }
            salvarProcesso(pn);
        }
    }
    
    /**Método responsável por alterar o estado da notificação de acordo com a sua etapa.
     * @param pn - objeto ProcessoNotificacao.
     * @param etapa - etapa do processo. 
     * @return atualizacaoEstado - Objeto AtualizacaoEstado.
     */
    public AtualizacaoEstado AtualizaEstadoNotificacao(ProcessoNotificacao pn,Integer etapa){
        AtualizacaoEstado atualizacaoEstado = criaObjeto(AtualizacaoEstado.class);
        
        atualizacaoEstado.setFuncionario(pn.getNotificador());
        
        if (etapa == 1) {
            atualizacaoEstado.setDataAtualizacaos(pn.getDataAbertura());
            atualizacaoEstado.setEstadoNotificacao(AGUARDANDOANALISEOBITO);
        } 
        else {
            if(etapa == 2){
                    atualizacaoEstado.setDataAtualizacaos(pn.getDataAbertura());
                    atualizacaoEstado.setEstadoNotificacao(AGUARDANDOANALISEENTREVISTA);
               }
                else {
                    if(etapa == 3){
                        atualizacaoEstado.setDataAtualizacaos(pn.getDataAbertura());
                        atualizacaoEstado.setEstadoNotificacao(AGUARDANDOANALISECAPTACAO);
                    }
                }    
            }
            return atualizacaoEstado;
        }    
    
    
    /**Método responsável por salvar no banco  de dados o estado da notificação.
     * @param ae - objeto AtualizacaoEstado.
     */
    public void salvaEstadoNotificacao(AtualizacaoEstado ae){
        atualizacaoEstadoRepository.save(ae);
    }
    
    /**Método responsável por criar um processo de notificação randomico até a
     * etapa de Analise de Óbito.
     * @param df - instancia DataFactory.
     * @return processoNotificacao - objeto ProcessoNotificacao.
     */
    public ProcessoNotificacao criarAnaliseObito(DataFactory df){

        ProcessoNotificacao processoNotificacao = criaObjeto(ProcessoNotificacao.class);
        Calendar dataAbertura = Calendar.getInstance();
        Obito obito = obitoData.criaObito(df);
        Date dataIni = removeDias(obito.getDataObito().getTime());

        
        processoNotificacao.setArquivado(false);
        List<Notificador> listNotificador = notificadorRepository.findAll();
        processoNotificacao.setNotificador(df.getItem(listNotificador));
        processoNotificacao.setObito(obito);
        dataAbertura.setTime(df.getDateBetween(dataIni, obito.getDataObito().getTime()));
        processoNotificacao.setDataAbertura(dataAbertura);
        processoNotificacao.setCodigo(df.getNumberText(8));
        processoNotificacao.setEntrevista(null);
        
        return processoNotificacao;
    }
    
    /**Método responsável por salvar no banco de dados um Objeto ProcessoNotificacao.
     * @param pn - Objeto ProcessoNotificacao.
     */
    public void salvarProcesso(ProcessoNotificacao pn){
        processoNotificacaoRepository.save(pn);
    }
    
    /**Método responsável por criar uma entrevista.
     *@param df - instancia DataFactory.
     * @return entrevista - Objeto entrevista.
     */
    public Entrevista criarEntrevista(DataFactory df){
        return entrevistaData.criaEntrevista(df);
    }
    
    /**Método responsável por criar processo de notificação randomico até a
     * etapa de Analise de Entrevista de forma randomica.
     * @param df - instancia DataFactory.
     * @param QtdEnt - quantidade de processos.
     */
    @SuppressWarnings("unused")
    public void criaEntrevistaRadom(DataFactory df,Integer QtdEnt){
     for (int i = 0; i < QtdEnt;i++){
            ProcessoNotificacao pn = criarAnaliseObito(df);
            List<AtualizacaoEstado> listAtualizacao = new ArrayList<>();
            listAtualizacao.add(AtualizaEstadoNotificacao(pn,1));
            listAtualizacao.add(AtualizaEstadoNotificacao(pn,2));
            pn.setEntrevista(criarEntrevista(df));
            pn.setHistorico(listAtualizacao);
            
            for(AtualizacaoEstado ae : listAtualizacao){
                pn.setUltimoEstado(ae);
            }
            salvaEstadoNotificacao(AtualizaEstadoNotificacao(pn,2));
            salvarProcesso(pn);
        }
    }
    
    /**Método responsável por criar uma Captacao.
     * @param df - instancia DataFactory.
     * @return captacao - Objeto Captacao.
     */
    public Captacao criaCaptacao(DataFactory df){
        List<Captador> listCaptador = captadorRepository.findAll();
        return captacaoData.criarCaptacao(df, df.getItem(listCaptador));
    } 
    
    /**Método responsável por criar processo de notificação randomico até a
     * etapa de Analise de Captacao de forma randomica.
     * @param df - instancia DataFactory.
     * @param QtdCap - quantidade de processos.
     */
    public void criaCaptacaoRadom(DataFactory df,Integer QtdCap){
     for (int i = 0; i < QtdCap;i++){
            ProcessoNotificacao pn = criarAnaliseObito(df);
            List<AtualizacaoEstado> listAtualizacao = new ArrayList<>();
            
            listAtualizacao.add(AtualizaEstadoNotificacao(pn,1));
            listAtualizacao.add(AtualizaEstadoNotificacao(pn,2));
            listAtualizacao.add(AtualizaEstadoNotificacao(pn,3));
            
            pn.setEntrevista(criarEntrevista(df));
            pn.setCaptacao(criaCaptacao(df));
            pn.setHistorico(listAtualizacao);
            
            for(AtualizacaoEstado ae : listAtualizacao){
                pn.setUltimoEstado(ae);
            }
            
            salvarProcesso(pn);
        }
    }
    
    private static Date removeDias(Date date) {
            GregorianCalendar gc = new GregorianCalendar();  
            gc.setTime(date);  
            gc.set(Calendar.DATE, gc.get(Calendar.DATE) - 2);
            return gc.getTime();  
    } 
}
