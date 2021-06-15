package test.demo2;


import org.utn.alg2.grp3.anotations.Injected;

public class FordFiesta implements Auto
{
	@Injected(implementation=ToyotaMotor.class)
	private Motor motor;
		
//	@Injected(implementation=BujiaBosh.class, count=5)
//	private List<Bujia> bujias;

	@Override
	public void acelerar()
	{
		motor.carburar();
		System.out.println("Acelerando el fiestita!");
	}

	@Override
	public void frenar()
	{
		System.out.println("Frenando...");
	}

	@Override
	public void regular()
	{
		System.out.println("Regulando...");
	}

}
