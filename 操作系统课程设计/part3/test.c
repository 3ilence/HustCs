#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>

#define MEM_CLEAR 1

int main(int argc, char **argv) {
  int fd, start, num;
  char buf[1024];
  fd = open("./Silence_Drive/Silence_Drive.ko", O_RDWR);
  if (fd < 0) {
    printf("Open error!\n");
    return 0;
  }
  if (argc == 3 && strncmp(argv[1], "read", 4) == 0) {
    start = 0;
    num = atoi(argv[2]);
    lseek(fd, start, SEEK_SET);
    read(fd, buf, num);
    printf("Read: %s\n", buf);
  }
  else if (argc == 3 && strncmp(argv[1], "write", 5) == 0) {
    start = 0;
    lseek(fd, start, SEEK_CUR);
    write(fd, argv[2], strlen(argv[2]));
    printf("Write succeed!\n");
  }
  else if (argc == 3 && strncmp(argv[1], "ioctl", 5) == 0) {
    if (strncmp(argv[2], "clear", 5) == 0) {
      ioctl(fd, MEM_CLEAR, NULL);
      printf("Clear success!\n");
    }

  }


  close(fd);
  return 0;
}