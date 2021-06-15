package test.demo;

import org.utn.alg2.grp3.anotations.Injected;

public class TheBeatles implements Banda
{
   @Injected(implementation=GeorgeHarrison.class)
   private Guitarrista primeraGuitarra;

   @Injected(implementation=JohnLennon.class)
   private Guitarrista segundaGuitarra;

   @Injected(implementation=PaulMcCartney.class)
   private Bajista bajista;

   @Injected(implementation=RingoStarr.class)
   private Baterista baterista;

   public String toString()
   {
      String ret = "";
      ret+="The Beatles { \n";
      ret+="       "+primeraGuitarra+"\n";
      ret+="      ,"+segundaGuitarra+"\n";
      ret+="      ,"+bajista+"\n";
      ret+="      ,"+baterista+" }\n";
      return ret;
   }

   public Guitarrista getPrimeraGuitarra()
   {
      return primeraGuitarra;
   }
   public void setPrimeraGuitarra(Guitarrista primeraGuitarra)
   {
      this.primeraGuitarra=primeraGuitarra;
   }

   public Guitarrista getSegundaGuitarra()
   {
      return segundaGuitarra;
   }
   public void setSegundaGuitarra(Guitarrista segundaGuitarra)
   {
      this.segundaGuitarra=segundaGuitarra;
   }

   public Bajista getBajista()
   {
      return bajista;
   }
   public void setBajista(Bajista bajista)
   {
      this.bajista=bajista;
   }

   public Baterista getBaterista()
   {
      return baterista;
   }
   public void setBaterista(Baterista baterista)
   {
      this.baterista=baterista;
   }

}