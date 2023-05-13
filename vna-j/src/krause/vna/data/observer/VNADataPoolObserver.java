package krause.vna.data.observer;



public interface VNADataPoolObserver extends VNAObserver{
	public enum CHANGEDOBJECT {
		SCANMODE
	};

	public void dataChanged(CHANGEDOBJECT changedObjectType, Object oldData, Object newData);

}
