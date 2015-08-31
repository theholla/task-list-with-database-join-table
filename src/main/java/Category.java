import java.util.List;
import org.sql2o.*;
import java.util.ArrayList;

public class Category {
  private int id;
  private String name;

  public Category(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public static List<Category> all() {
    String sql = "SELECT id, name FROM Categories";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Category.class);
    }
  }

  @Override
  public boolean equals(Object otherCategory){
    if (!(otherCategory instanceof Category)) {
      return false;
    } else {
      Category newCategory = (Category) otherCategory;
      return this.getName().equals(newCategory.getName());
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO Categories(name) VALUES (:name)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Category find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM Categories where id=:id";
      Category Category = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Category.class);
      return Category;
    }
  }

  public void addTask(Task task) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories_tasks (category_id, task_id)" +
                   " VALUES (:category_id, :task_id)";
      con.createQuery(sql)
        .addParameter("category_id", this.getId())
        .addParameter("task_id", task.getId())
        .executeUpdate();
    }
  }

  public ArrayList<Task> getTasks() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT task_id FROM categories_tasks WHERE category_id = :category_id";
      //all the task_ids that match the category_id are returned as a List<Integer>
      List<Integer> taskIds = con.createQuery(sql)
        .addParameter("category_id", this.getId())
        .executeAndFetch(Integer.class);

      //create an empty array list to hold the new Task objects
      ArrayList<Task> tasks = new ArrayList<Task>();

      //loop through task_ids
      for (Integer taskId : taskIds) {
        String taskQuery = "SELECT * FROM tasks WHERE id = :task_id";
        //for each id, create a new query that fetches the task and adds to [tasks]
        Task task = con.createQuery(taskQuery)
          .addParameter("task_id", taskId)
          .executeAndFetchFirst(Task.class);
        tasks.add(task);
      }
      return tasks;
    }
  }

  public void delete() {
      try(Connection con = DB.sql2o.open()) {
        String deleteQuery = "DELETE FROM categories WHERE id = :id;";
          con.createQuery(deleteQuery)
            .addParameter("id", id)
            .executeUpdate();

        String joinDeleteQuery = "DELETE FROM categories_tasks WHERE category_id = :category_id";
          con.createQuery(joinDeleteQuery)
            .addParameter("category_id", this.getId())
            .executeUpdate();
      }
    }

}
