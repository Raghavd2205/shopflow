# Stage 1 — Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files first — for layer caching
COPY package*.json ./

# Install all dependencies including dev
RUN npm install

# Copy source code
COPY . .

# Build NestJS app
RUN npm run build

# Stage 2 — Production
FROM node:20-alpine

WORKDIR /app

# Copy only production dependencies
COPY package*.json ./
RUN npm install --only=production

# Copy built files from builder stage
COPY --from=builder /app/dist ./dist

EXPOSE 3000

CMD ["node", "dist/main.js"]