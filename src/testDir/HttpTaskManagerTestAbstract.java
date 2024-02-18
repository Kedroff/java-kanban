package testDir;

public abstract class HttpTaskManagerTestAbstract {
    protected abstract void testAddTasks();
    protected abstract void testLoad();
    protected abstract void testSave();

    protected abstract void testGetAllTasksAfterLoad();

    protected abstract void testGetAllEpicsAfterLoad();

    protected abstract void testGetAllSubtasksAfterLoad();

    protected abstract void testGetAllPrioritizedAfterLoad();

    protected abstract void testGetAllHistoryAfterLoad();

}
