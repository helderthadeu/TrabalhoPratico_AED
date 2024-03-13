import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Data {
    private int dia;
    private int mes;
    private int ano;

    public Data() {

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

    public Data(int d, int m, int a) {
        this.ano = a;
        this.dia = d;
        this.mes = m;
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
     * Retorna a data em uma string no formato dd/mm/aaaa
     */
    public String toString() {
        return this.dia + "/" + this.mes + "/" + this.ano;
    }

    /**
     * Função que converte a data para números de dias
     * 
     * @return retorna o número de dias
     */
    public int toDias() {

        int diasMes = 0, diasAno;

        LocalDate dataOriginal = LocalDate.of(this.ano,this.mes,this.dia);  
        LocalDate anoZero = LocalDate.of( 0,1,1);

        return (int)(ChronoUnit.DAYS.between(anoZero, dataOriginal)); 
    }

    public void toDate(long dias) {
        LocalDate dataConvet = converterDiasParaData((long)(dias));
        this.dia = dataConvet.getDayOfMonth();
        this.mes = dataConvet.getMonthValue();
        this.ano = dataConvet.getYear();
        
    }
    private static LocalDate converterDiasParaData(long dias) {
        LocalDate dataBase = LocalDate.of( 0,1,1).plusDays(dias);
        return dataBase;
    }
}
