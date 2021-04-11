#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/cdev.h>
#include <linux/string.h>
#include <linux/slab.h>
#include <linux/fs.h>
#include <linux/uaccess.h>

#define MEM_SIZE 0x4000  //缓冲区大小
#define GLOBAL_MAJOR 500  //设备号
#define MEM_CLEAR 1

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("A  character driver module");

struct silence_dev {
  	struct cdev cdev;
  	unsigned char mem[MEM_SIZE];
};

static struct silence_dev *silence_devp;
struct device *device;
struct class *class;


static int driver_open(struct inode *inodep, struct file *filep) {
  	printk(KERN_INFO "silence Driver: open\n");
  	filep->private_data = silence_devp;
  	return 0;
}


static int driver_release(struct inode *inodep, struct file *filep) {
  	printk(KERN_INFO "silence Driver: release\n");
  	return 0;
}


static ssize_t driver_read(struct file *filep, char __user *buf, size_t count, loff_t *offset)
{
    int ret = 0;
  	printk(KERN_INFO "silence Driver: start read\n");

  	size_t avail = MEM_SIZE - *offset;//剩余可读字符数量
  	struct silence_dev *dev = filep->private_data;//

	//count小于缓冲区剩余可读大小
  	if (count <= avail) {
    	if (copy_to_user(buf, dev->mem + *offset, count) != 0)
      		return -EFAULT;
    	*offset += count;
    	ret = count;
  	}
	//count大于缓冲区剩余可读大小
  	else {
    	if (copy_to_user(buf, dev->mem + *offset, avail) != 0)
      		return -EFAULT;
    	*offset += avail;
    	ret = avail;
  	}

  	printk(KERN_INFO "silence Driver: read %u bytes\n", ret);
  	return ret;
}


static ssize_t driver_write(struct file *filep, const char __user *buf, size_t count, loff_t *offset)
{
	int ret = 0;
  	printk(KERN_INFO "silence Driver: start write\n");

  	size_t avail = MEM_SIZE - *offset;
  	struct silence_dev *dev = filep->private_data;
  	memset(dev->mem + *offset, 0, avail);
  	printk(KERN_INFO "silence Driver: After write\n");

  	if (count > avail) {
    	if (copy_from_user(dev->mem + *offset, buf, avail) != 0)
      		return -EFAULT;
    	*offset += avail;
    	ret = avail;
  	}
  	else {
    	if (copy_from_user(dev->mem + *offset, buf, count) != 0)
      		return -EFAULT;
    	*offset += count;
    	ret = count;
  	}
  	printk(KERN_INFO "silence Driver: written %u bytes\n", ret);
  	return ret;
}


//设定write或者read操作正确的起始下标
static loff_t driver_llseek(struct file *filep, loff_t offset, int whence)
{
	loff_t ret = 0;
  	printk(KERN_INFO "silence Driver: start llseek\n");

  	switch (whence) {
  	//当输入的偏移小于0或者大于缓冲区大小
  	case 0:
    	if (offset < 0) {
      		ret = -EINVAL;
      		break;
    	}
    	if (offset > MEM_SIZE) {
      		ret = -EINVAL;
      		break;
    	}
    	ret = offset;
    	break;
	//输入的偏移大小合法
  	case 1:
	  	//f_pos是文件当前位置
		//判断当前位置+偏移是否越界
    	if ((filep->f_pos + offset) > MEM_SIZE) {
      		ret = -EINVAL;
      		break;
    	}
    	if ((filep->f_pos + offset) < 0) {
     	 	ret = -EINVAL;
      		break;
    	}
    	ret = filep->f_pos + offset;
    	break;
  	default:
    	ret = -EINVAL;
  	}

  	if (ret < 0)
    	return ret;

  	printk(KERN_INFO "silence Driver: set offset to %u\n", ret);
  	filep->f_pos = ret;
  	return ret;
}

//清除设备缓冲区
static long driver_ioctl(struct file *filep, unsigned int cmd, unsigned long arg)
{
	struct silence_dev *dev = filep->private_data;
  	printk(KERN_INFO "silence Driver: start memory clear\n");
  	switch (cmd) {
  		case MEM_CLEAR:
    		memset(dev->mem, 0, MEM_SIZE);
    		printk("silence Driver: memory is set to zero\n");
    		break;
  		default:
    		return -EINVAL;
  	}
  	return 0;
}


static const struct file_operations fops = {
  .owner = THIS_MODULE,
  .open = driver_open,
  .release = driver_release,
  .read = driver_read,
  .write = driver_write,
  .llseek = driver_llseek,
  .unlocked_ioctl = driver_ioctl,
};


static int __init silencedriver_init(void) {
    int ret;
    dev_t devno = MKDEV(GLOBAL_MAJOR, 0);
  	printk(KERN_INFO "Load module: silence_driver\n");
	//注册字符设备编号
  	ret = register_chrdev_region(devno, 1, "silence_driver");
  	if (ret < 0) {
    	printk(KERN_ALERT "Registering the character device failed with %d\n", ret);
    	return ret;
  	}

  	//参数一为要分配的块的大小
  	silence_devp = kzalloc(sizeof(struct silence_dev), GFP_KERNEL);
  	if (silence_devp == NULL) {
    	printk(KERN_ALERT "Alloc memory for device failed\n");
    	ret = -ENOMEM;
    	goto failed;
  	}
	//缓冲区初始化
  	memset(silence_devp->mem, 0, MEM_SIZE);

  	//静态初始化字符设备
  	cdev_init(&silence_devp->cdev, &fops);
  	silence_devp->cdev.owner = THIS_MODULE;
	//传入 cdev 结构的指针，起始设备编号，以及设备编号范围
  	cdev_add(&silence_devp->cdev, devno, 1);

  	/* Creat device file */
 	class = class_create(THIS_MODULE, "silence_driver");
  	if (IS_ERR(class)) {
    	ret = PTR_ERR(class);
    	printk(KERN_ALERT "Creat class for device file failed with %d\n", ret);
    	goto failed;
  	}
  	device = device_create(class, NULL, devno, NULL, "silence_driver");
 	if (IS_ERR(device)) {
    	class_destroy(class);
    	ret = PTR_ERR(device);
    	printk(KERN_ALERT "Creat device file failed with %d\n", ret);
    	goto failed;
  	}

  	return 0;

 	failed:
  		unregister_chrdev_region(devno, 1);
  	return ret;
}


static void __exit silencedriver_exit(void) {
  printk(KERN_INFO "Unload module: silence_driver\n");

  device_destroy(class, MKDEV(GLOBAL_MAJOR, 0));
  class_unregister(class);
  class_destroy(class);

  cdev_del(&silence_devp->cdev);
  kfree(silence_devp);
  unregister_chrdev_region(MKDEV(GLOBAL_MAJOR, 0), 1);
}

module_init(silencedriver_init);
module_exit(silencedriver_exit);