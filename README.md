# spring-building-blocks
A simple library providing functionality for my spring-boot projects that I often miss and/or re-use. For now you should
treat all the features included in this library as experimental. This is not particularly recommended for production as 
of yet.

# Database additions
This sections covers all the implemented additions to working with the database using Spring/JPA/Hibernate

## Views
For some reason views are still not part of the normal JPA workflow so I decided to make an implementation
if this powerful feature.

### Usage:
To enable Views use the @EnableSpringBB annotation inside any of your @Configuration classes first. Without
this enabled no SpringBB functionality is enabled at all.

You can define a @View on top of any entity right now. the @View annotation has these options:
- name - the name of this view
- where - a valid WHERE clause
- [tableName] - optional parameter for referencing a specific table name

by default the tableName parameter is read from the @Table annotation however you can override this
with the tableName parameter. If none of these are present by default the table name is a snake_case version
of the entities name.

simple example of usage would be something like this:
```
@Entity
@Table(name = "authors")
@View(name = "active_authors", where = "deleted = FALSE")
public class Author {
    ...
}
```

this is parsed ahead of time using an AnnotationProcessor into a views.sql file inside the class path and is executed
upon launch if it is present. example output of the above case would be:  
`CREATE OR REPLACE VIEW active_authors AS SELECT * FROM authors WHERE deleted = FALSE;`  
The whole "where" parameter is essentially appended into the wHERE clause as a string, so you can write whatever you 
want there is long as your database can understand it!

Then you are able to reference `active_authors` directly from within your repositories using a native query for example:
```
    @Query(value = "SELECT * FROM active_authors", nativeQuery = true)
    List<Author> findAllActive();
```

These views always select all the fields from the viewed entity for now as this is the simplest case while also being
quite helpful. for instance your definition of what an active user is might change based on many parameters like:
- is the user deleted?
- is the user banned?
- is the user online?
- was he online in the last month?

Without a view you have to maintain all your queries everytime a new such definition of "activity" is added, and you 
always have to include all of these into the WHERE clause manually, which is horrible to maintain... Instead with @View
you can maintain all repository queries regarding only active users in a single line and on top of that views are also 
maintained by the Database server, which means you'll get optimized performance by not pushing all the parameters into
the query you're executing every time, not to mention readability inside your repositories!

@View is a repeatable annotation which means you can put as many as you like above an entity, however I find usually
you'll only need a couple anyway. For example in a social network or similar you might want an active_users view to try
and match active users with each other by recommending friendship between them and inactive_users view to send them an
email from time to time (please don't do that...).

Right now complex views with joined tables are not supported yet but I'm planning to give them some support soon.