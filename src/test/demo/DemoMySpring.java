package test.demo;

import org.utn.alg2.grp3.anotations.ComponentScan;
import org.utn.alg2.grp3.di.Factory;

@ComponentScan("test.demo")
public class DemoMySpring
{
   public static void main(String[] args)
   {
      Banda banda = Factory.getObject(TheBeatles.class);
      System.out.println(banda.toString());
   }
}
