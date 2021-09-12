### 是否可以完全将MySQL的数据移植到ES？

https://www.quora.com/Is-it-worth-migrating-from-MySQL-to-Elasticsearch

It is not a good idea to completely move away from MySQL to Elasticsearch :-). Though Elasticsearch is awesome at search, analysing documents, logs and analytics, moving away from MySQL you would face many technical challenges. Remember that ES do not handle **transactional** and have to be careful when dealing with updates and deletes.

Moving away would throw you some technical challenges such as

- Mapping your MySQL Database tables to ES schema / document structure
- Dealing with relationship gets tricker and harder while the requirements grows. What I mean here is, in MySQL you have relationship between tables and it is easy for you to update/delete based on foreign key relationship. However in ES, it makes it difficult relate two types (Tables) and you would have to have a strategy to link two types together.
- Dealing with updates and deletes is very chaotic has have to deal with related types (types in ES is equivalent to tables in DB)

If you already have existing application runs on MySQL, I would still stick to MySQL as a **Primary Data Store** and use ES a **Secondary Data Store** for the requirements where you need to have heavy search. So your MySQL would be a single source of truth and you could always flatten the tables and Index that data to ES and for search related to queries, you query ES and use MySQL as is for usual application-db transactions.

