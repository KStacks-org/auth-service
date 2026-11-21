# syntax=docker/dockerfile:1
# Multi-stage build for optimized native image
# Stage 1: Build
FROM ghcr.io/graalvm/native-image-community:25-muslib AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

ARG GITHUB_USERNAME

# Download dependencies (cached if pom.xml doesn't change)
RUN --mount=type=secret,id=github_token,env=GITHUB_TOKEN,required=true \
    ./mvnw -s .mvn/settings.xml dependency:go-offline

# Copy source code
COPY src ./src

# Build the application as a native image
RUN --mount=type=secret,id=github_token,env=GITHUB_TOKEN,required=true \
    ./mvnw -s .mvn/settings.xml clean native:compile -Pnative -DskipTests

# Stage 2: Runtime
FROM alpine:latest

WORKDIR /app

# Create a non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install dependencies needed by Spring Boot native image
RUN apk add --no-cache tzdata ca-certificates bash wget

# Copy the native executable from builder stage
COPY --from=builder /app/target/auth app

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

USER appuser

# Expose application port
EXPOSE 1867

# Set prod profile by default (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider "http://localhost:${SERVER_PORT:-1867}${SERVER_SERVLET_CONTEXT_PATH:-/auth}/actuator/health/liveness" || exit 1

# Run the application
ENTRYPOINT ["./app"]
