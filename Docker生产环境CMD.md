# Docker生产环境CMD



docker run --name docker-nginx -p 80:80 -v ~/docker-nginx/pallet:/usr/share/nginx/pallet -v ~/docker-nginx/conf.d:/etc/nginx/conf.d -d nginx



docker run  --name docker-jsonserver -p 3001:3000  -v ~/docker-jsonserver/data:/data williamyeh/json-server --watch db.json

