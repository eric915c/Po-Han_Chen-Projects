package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class client_Ticker implements Runnable{
	ActionListener al;
	private boolean isTicking;
	Thread t;
	int delay;
	public client_Ticker(int i, ActionListener actionlistener){
		al = actionlistener;
		delay = i;
		t = new Thread(this);
		t.start();
		isTicking = false;
	}

	public client_Ticker(int i){
		delay = i;
		t = new Thread(this);
		t.start();
		isTicking = false;
	}

	public void addActionListener(ActionListener actionlistener){
		if(al == null)
			al = actionlistener;
		else
			System.out.println("WARNING: ActionListener already added to client_Ticker.");
	}

	public boolean isRunning(){
		return isTicking;
	}

	public void start(){
		isTicking = true;
	}

	public void stop(){
		isTicking = false;
	}

	public void setDelay(int i){
		delay = i;
	}

	public int getDelay(){
		return delay;
	}

	private void fireActionPerformed(){
		if(al == null || !isTicking)
		{
			return;
		} else{
			ActionEvent actionevent = new ActionEvent(this, 0, null);
			al.actionPerformed(actionevent);
			return;
		}
	}

	public void run(){
		do{
			fireActionPerformed();
			try{
				Thread.sleep(delay);
			}
			catch(InterruptedException interruptedexception){
				System.out.println("WARNING: client_Ticker thread interrupted.");
			}
		} while(true);
	}
}

