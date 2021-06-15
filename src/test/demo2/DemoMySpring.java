package test.demo2;


import org.utn.alg2.grp3.anotations.ComponentScan;
import org.utn.alg2.grp3.di.Factory;

@ComponentScan("test.demo2")
public class DemoMySpring
{
   public static void main(String[] args)
   {
      Auto auto = Factory.getObject(FordFiesta.class);
      auto.regular();
      auto.acelerar();
      auto.frenar();
   }
}
