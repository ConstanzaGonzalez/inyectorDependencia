package test.demo2;

import frm.myspring.ComponentScan;
import frm.myspring.Factory;

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
