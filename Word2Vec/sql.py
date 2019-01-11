import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy.engine.url import URL

def init_db():
    connect_url = URL(
            'mysql+pymysql',
            username='-',
            password='-',
            host="-",
            database='-')
            
    engine = create_engine(connect_url)
    return engine

# con = init_db()

# table_names = pd.read_csv("table_names.csv", header=None, encoding="utf-8", sep="\t")
# table_names = table_names.iloc[:, 2]
# print(table_names)

# for table in table_names:
#     pars = pd.read_sql("SELECT * FROM %s" % table, con=con)
#     pars.to_csv("%s.csv" % table, sep="\t", header=False, index=False, encoding="utf-8")
