package tp2;

public class Registro {
    private int id;
    private long equilibrio;

    public Registro(){
        this.id = -1;
        this.equilibrio = -1;
    }
    public Registro(int id, long a){
        this.id = id;
        this.equilibrio = a;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public long getEquilibrio(){
        return equilibrio;
    }
    public void setEquilibrio(long a){
        this.equilibrio = a;
    }

}
