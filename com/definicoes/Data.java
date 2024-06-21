package com.definicoes;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Data {
    private int dia;
    private int mes;
    private int ano;

    public Data() {

    }

    public Data(int a, int m, int d) {
        this.ano = a;
        this.mes = m;
        this.dia = d;
        
    }

    public Data(long dias) {
        this.toDate(dias);
    }

    public int getDia() {
        return dia;
    }

    public int getAno() {
        return ano;
    }

    public int getMes() {
        return mes;
    }

    /**
     * Função que compara duas datas para saber qual é mais antiga
     * 
     * @param d Data comparada
     * @return true para caso o objeto que chama a função é mais velho e false para
     *         caso contrário
     */

    public boolean maisVelho(Data d) {
        if (this.ano > d.ano) {
            return false;
        } else if (this.mes > d.mes) {
            return false;
        } else if (this.dia >= d.dia) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @param Retorna a data em uma string no formato dd/mm/aaaa
     */
    public String toString() {
        return this.dia + "/" + this.mes + "/" + this.ano;
    }

    /**
     * Função que converte a data para números de dias
     * 
     * @return retorna o número de dias
     */
    public long toDias() {

        LocalDate dataOriginal = LocalDate.of(this.ano, this.mes, this.dia);
        LocalDate anoZero = LocalDate.of(1800, 1, 1);

        return (ChronoUnit.DAYS.between(anoZero, dataOriginal));
    }

    /**
     * Função que converte uma quantidade de dias para a data do elemento atual
     * 
     * @param dias Número de dias a ser convertido
     */
    public void toDate(long dias) {
        LocalDate dataConvet = converterDiasParaData((long) (dias));
        this.dia = dataConvet.getDayOfMonth();
        this.mes = dataConvet.getMonthValue();
        this.ano = dataConvet.getYear();

    }

    /**
     * Função auxiliar para retornar a data baseada no ano 0
     * 
     * @param dias número de dias a ser convertido emd ata
     * @return Retorna a data no formato LocalDate
     */
    private LocalDate converterDiasParaData(long dias) {
        LocalDate dataBase = LocalDate.of(1800, 1, 1).plusDays(dias);
        return dataBase;
    }
}
