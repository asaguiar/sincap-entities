package br.ifes.leds.reuse.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;


/**
 * Created by aleao on 11/08/15.
 */
@org.aspectj.lang.annotation.Aspect
public class Aspect {


    private final Logger logger = Logger.getLogger(this.getClass());
    private StringBuffer logMessage = new StringBuffer();

    private void print(JoinPoint joinPoint)
    {
        BasicConfigurator.configure();
        logMessage.append("Classe: ");
        logMessage.append(joinPoint.getTarget().getClass().getSimpleName());
        logMessage.append(" Método: ");
        logMessage.append(joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs();
        for (Object arg: args) {
            logMessage.append(" Argumento: "+arg.toString());
        }
        logger.setLevel(Level.INFO);
        logger.info(logMessage.toString());
        logMessage.setLength(0);

    }

    private void print(JoinPoint joinPoint, Throwable error )
    {

        print(joinPoint);
        logger.setLevel(Level.WARN);
        logMessage.append(" Exceção: " + error);
        logger.warn(logMessage.toString());
    }

//    @Before("execution(* br.ifes.leds.sincap.*.cln.cgt.*.salvar*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//
//        print(joinPoint);
//    }



    @Around("execution(* br.ifes.leds.sincap.*.cln.cgt.*.salvar*(..))")
    public void logAround(JoinPoint joinPoint) throws Throwable {

        print(joinPoint);

    }
//
//    @After("execution(* br.ifes.leds.sincap.*.cln.cgt.*.salvar*(..))")
//    public void logAfter(JoinPoint joinPoint) {
//
//        print(joinPoint);
//
//    }

    @AfterThrowing(
            pointcut = "execution(* br.ifes.leds.sincap.*.cln.cgt.*.salvar*(..))",
            throwing= "error")
    public void logAfterThrowing(JoinPoint joinPoint,Throwable error) {

        print(joinPoint,error);

    }


}
