package test.demo;

import org.utn.alg2.grp3.anotations.Injected;

public class RingoStarr implements Baterista
{
   @Override
   public String toString()
   {
      return "Ringo Starr";
   }

//   @Injected(implementation=TheBeatles.class)
   private Banda banda;

   public Banda getBanda() { return banda; }
   public void setBanda(Banda banda) { this.banda = banda; }
}
