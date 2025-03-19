package nz.ac.wgtn.ecs.jdala.utils;

import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;

/**
 * @author Quinten Smit
 */
public class IsolatedData {
    private Thread currentThread = Thread.currentThread();
    private Object transferObject;

    public IsolatedData(){}

    public Thread getCurrentThread() {
        return currentThread;
    }

    public void exitTransferState(Object transferObject) {
        if (!isInTransferState()){
            throw new DalaCapabilityViolationException(transferObject + " can't exit transfer state. object isn't in transfer state.");
        } else if (this.transferObject != transferObject){
            throw new DalaCapabilityViolationException("Isolated object must exit same portal object as it entered! entered: " + this.transferObject + " exited: " + transferObject);
        }
        this.transferObject = null;
        currentThread = Thread.currentThread();
    }

    public void enterTransferState(Object transferObject) {
        if (isInTransferState()){
            throw new DalaCapabilityViolationException(transferObject + " can't enter transfer state. It is already in transfer state.");
        }
        this.transferObject = transferObject;
    }

    public boolean isInTransferState() {
        return transferObject != null;
    }

    public Object getTransferObject() {
        return transferObject;
    }

    public String toString(){
        return "{Thread: " + currentThread + " TransferState: " + (transferObject == null ? "false" : "true (" + transferObject + ")") + " }";
    }
}
