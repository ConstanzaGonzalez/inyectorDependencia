package test.demo;

import frm.myspring.ComponentScan;
import frm.myspring.Factory;

@ComponentScan("test.demo")
public class DemoMySpring
{
   public static void main(String[] args)
   {
      Banda banda = Factory.getObject(TheBeatles.class);
      System.out.println(banda.toString());
   }
}
