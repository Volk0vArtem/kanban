package managers;

import tasks.AbstractTask;
import utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public List<AbstractTask> getHistory() {
        return history.getTasks();
    }

    @Override
    public void addToHistory(AbstractTask task) {
        history.add(task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}

class CustomLinkedList{
    private Node<AbstractTask> head;
    private Node<AbstractTask> tail;
    private int size = 0;
    private HashMap<Integer,Node> nodeMap;

    public CustomLinkedList(){
        nodeMap = new HashMap<>();
    }

    public void linkLast(AbstractTask data){
        Node<AbstractTask> last = tail;
        Node<AbstractTask> newNode = new Node<>(tail, data, null);
        tail = newNode;
        if (last == null){
            head = newNode;
        } else {
            last.next = newNode;
        }
        size++;
    }

    public List<AbstractTask> getTasks(){
        ArrayList<AbstractTask> taskList = new ArrayList<>();
        Node<AbstractTask> node = head;
        while (node != null){
            AbstractTask task = node.data;
            taskList.add(task);
            node = node.next;
        }
        return taskList;
    }

    public void removeNode(Node<AbstractTask> node){
        Node<AbstractTask> next = node.next;
        Node<AbstractTask> prev = node.prev;
        if (next == null && prev == null){
            head = null;
            tail = null;
        }else if (next == null){
            prev.next = null;
            tail = prev;
        } else if (prev == null){
            next.prev = null;
            head = next;
        } else {
            next.prev = prev;
            prev.next = next;
        }
        nodeMap.remove(node.data);
        size--;
    }

    public void remove(int id){
        removeNode(nodeMap.get(id));
    }

    public void add(AbstractTask task){
        if (nodeMap.containsKey(task.getId())){
            removeNode(nodeMap.get(task.getId()));
        }
        linkLast(task);
        nodeMap.put(task.getId(), tail);
    }

}

