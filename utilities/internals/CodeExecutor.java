package me.wonk2.utilities.internals;

import me.wonk2.DFPlugin;
import me.wonk2.utilities.DFUtilities;
import me.wonk2.utilities.actions.Control;
import me.wonk2.utilities.actions.pointerclasses.brackets.Bracket;
import me.wonk2.utilities.actions.pointerclasses.brackets.ClosingBracket;
import me.wonk2.utilities.actions.Repeat;
import me.wonk2.utilities.actions.pointerclasses.brackets.RepeatingBracket;
import me.wonk2.utilities.actions.pointerclasses.Action;
import me.wonk2.utilities.actions.pointerclasses.Conditional;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public abstract class CodeExecutor {
	private static void executeThread(ObjectArrWrapper threadContents){
		long wait = 0;
		assignPointers(threadContents);
		//stringifyThread(threadContents); // DEBUG
		
		Object codeBlock = threadContents.get(0);
		while(codeBlock != null){
			
			if(Conditional.class.isAssignableFrom(codeBlock.getClass())){
				if(((Conditional) codeBlock).evaluateCondition()) codeBlock = ((Action) codeBlock).pointer;
				else codeBlock = ((Conditional) codeBlock).bracketPointer;
				
				continue;
			}
			
			switch(((Action) codeBlock).action){
				case "Wait":
					wait += DFUtilities.getWait(((Control) codeBlock).args, ((Control) codeBlock).tags);
					break;
					
				case "End":
					return;
					
				case "Skip":
					break;
					
				case "StopRepeat":
					break; //TODO
				
				default:
					Action temp = (Action) codeBlock;
					Bukkit.getScheduler().runTaskLater(DFPlugin.plugin, temp::invokeAction, wait);
			}
			
			codeBlock = ((Action) codeBlock).pointer instanceof RepeatingBracket ?
				((RepeatingBracket) ((Action) codeBlock).pointer).pointer :
				((Action) codeBlock).pointer;
		}
		
	}
	
	public static void executeThread(Object[] threadContents){
		executeThread(new ObjectArrWrapper(threadContents));
	}
	
	private static ObjectArrWrapper assignPointers(ObjectArrWrapper threadContents){
		ArrayList<Repeat> repeats = new ArrayList<>();
		for(int i = 0; i < threadContents.length; i++) {
			Object codeBlock = threadContents.get(i);
			if(codeBlock instanceof RepeatingBracket) ((RepeatingBracket) codeBlock).pointer = repeats.get(repeats.size() - 1);
			if(isBracket(codeBlock)) continue;
			
			((Action) codeBlock).setPointer(
				threadContents.get(i + 1) instanceof ClosingBracket ?
					threadContents.get(i + 2) :
					threadContents.get(i + 1)
			);
			
			if (Conditional.class.isAssignableFrom(codeBlock.getClass())){
				for(int k = i; k < threadContents.length - 1; k++)
					if(isBracket(threadContents.get(k)))
						((Conditional) codeBlock).bracketPointer = (Action) threadContents.get(k + 1);
				
				if(codeBlock instanceof Repeat) repeats.add((Repeat) codeBlock);
			}
		}
		
		return threadContents;
	}
	
	public static void stringifyThread(ObjectArrWrapper thread){
		for(int i = 0; i < thread.length; i++){
			Object codeBlock = thread.get(i);
			if(codeBlock instanceof Action && !(codeBlock instanceof Conditional))
				Bukkit.broadcastMessage(stringifyAction((Action) codeBlock));
			else if(codeBlock instanceof Conditional)
				Bukkit.broadcastMessage(stringifyAction((Conditional) codeBlock));
			else if(isBracket(codeBlock))
				Bukkit.broadcastMessage(codeBlock instanceof ClosingBracket ? "< ClosingBracket >" : "< RepeatingBracket > | -> " + stringifyAction(((RepeatingBracket) codeBlock).pointer));
		}
	}
	
	private static String stringifyAction(Action action){
		return action.action + " -> " + stringifyPointer(action.pointer);
	}
	
	private static String stringifyPointer(Object pointer){
		if(pointer instanceof Action) return ((Action) pointer).action;
		else return "< RepeatingBracket >";
	}
	
	private static boolean isBracket(Object obj){
		return Bracket.class.isAssignableFrom(obj.getClass());
	}
	
}
