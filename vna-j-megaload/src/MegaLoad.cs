using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.IO;
using Microsoft.Win32;
using System.Threading;

namespace MegaLoad_.NET
{
	/// <summary>
	/// Summary description for Form1.
	/// </summary>
	public class MegaLoadForm : System.Windows.Forms.Form
	{
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.TextBox FlashFileName;
		private System.Windows.Forms.Button FlashOpen;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.TextBox EEpromFileName;
		private System.Windows.Forms.Button EEpromOpen;
		private System.Windows.Forms.GroupBox groupBox3;
		private System.Windows.Forms.CheckBox BLB12;
		private System.Windows.Forms.CheckBox BLB11;
		private System.Windows.Forms.CheckBox BLB02;
		private System.Windows.Forms.CheckBox BLB01;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.GroupBox groupBox4;
		private System.Windows.Forms.GroupBox groupBox5;
		private System.Windows.Forms.GroupBox groupBox6;
		private System.Windows.Forms.ComboBox CommSpeed;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.CheckBox RTS;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.Label label5;
		private System.Windows.Forms.Label label6;
		private System.Windows.Forms.Label label7;
		private System.Windows.Forms.Label label8;
		private System.Windows.Forms.Label Device;
		private System.Windows.Forms.Label PageSize;
		private System.Windows.Forms.Label BootSize;
		private System.Windows.Forms.Label FlashSize;
		private System.Windows.Forms.Label EEpromSize;
		private System.Windows.Forms.GroupBox groupBox7;
		private System.Windows.Forms.GroupBox groupBox8;
		private System.Windows.Forms.GroupBox groupBox9;
		private System.Windows.Forms.ListBox MessageList;
        private System.Windows.Forms.ProgressBar ProgressBar;
		private System.Windows.Forms.OpenFileDialog OpenFileDialog;
		private System.Windows.Forms.ComboBox PortSelect;
		
		private byte[] Flash;
		private int FlashMin;
		private int FlashMax;

		private byte[] EEprom;
		private bool[] EEpromUse;
		private int EEpromMin;
		private int EEpromMax;
		private int BytePtr;

		private int PageSizeInt;
		private int FlashSizeInt;
		private int BootSizeInt;
		private int EEpromSizeInt;
		private int PagePtr;
		private int Retry;
		private char MemType;

        private bool Reg = false; 
		private string FlashFileNameHex = "";
		private string EEpromFileNameHex = "";
		private System.Windows.Forms.TextBox Status;
		private System.Windows.Forms.CheckBox DTR;
		private System.Windows.Forms.Button SendReset;
		private System.Windows.Forms.Button MonitorButton;

		private MonitorForm Monitor;
		private AboutForm About;
		private System.Windows.Forms.Button PortOpenClose;
		private System.Windows.Forms.GroupBox groupBox10;
		private System.Windows.Forms.Button AboutButton;
		private System.Windows.Forms.ToolTip ToolTip;
        private System.Windows.Forms.ListBox listBox1;
        private System.IO.Ports.SerialPort Comm1;
		private System.ComponentModel.IContainer components;

		public MegaLoadForm()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();
			//
			// TODO: Add any constructor code after InitializeComponent call
			//
		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
            Comm1.Close();// Comm.Active = false;
			if( disposing )
			{
				if (components != null) 
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MegaLoadForm));
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.FlashOpen = new System.Windows.Forms.Button();
            this.FlashFileName = new System.Windows.Forms.TextBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.EEpromOpen = new System.Windows.Forms.Button();
            this.EEpromFileName = new System.Windows.Forms.TextBox();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.label1 = new System.Windows.Forms.Label();
            this.BLB01 = new System.Windows.Forms.CheckBox();
            this.BLB12 = new System.Windows.Forms.CheckBox();
            this.BLB11 = new System.Windows.Forms.CheckBox();
            this.BLB02 = new System.Windows.Forms.CheckBox();
            this.groupBox4 = new System.Windows.Forms.GroupBox();
            this.PortOpenClose = new System.Windows.Forms.Button();
            this.RTS = new System.Windows.Forms.CheckBox();
            this.DTR = new System.Windows.Forms.CheckBox();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.CommSpeed = new System.Windows.Forms.ComboBox();
            this.PortSelect = new System.Windows.Forms.ComboBox();
            this.MonitorButton = new System.Windows.Forms.Button();
            this.SendReset = new System.Windows.Forms.Button();
            this.groupBox5 = new System.Windows.Forms.GroupBox();
            this.Device = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.PageSize = new System.Windows.Forms.Label();
            this.BootSize = new System.Windows.Forms.Label();
            this.FlashSize = new System.Windows.Forms.Label();
            this.EEpromSize = new System.Windows.Forms.Label();
            this.groupBox6 = new System.Windows.Forms.GroupBox();
            this.MessageList = new System.Windows.Forms.ListBox();
            this.groupBox7 = new System.Windows.Forms.GroupBox();
            this.Status = new System.Windows.Forms.TextBox();
            this.groupBox8 = new System.Windows.Forms.GroupBox();
            this.ProgressBar = new System.Windows.Forms.ProgressBar();
            this.groupBox9 = new System.Windows.Forms.GroupBox();
            this.listBox1 = new System.Windows.Forms.ListBox();
            this.OpenFileDialog = new System.Windows.Forms.OpenFileDialog();
            this.groupBox10 = new System.Windows.Forms.GroupBox();
            this.AboutButton = new System.Windows.Forms.Button();
            this.ToolTip = new System.Windows.Forms.ToolTip(this.components);
            this.Comm1 = new System.IO.Ports.SerialPort(this.components);
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.groupBox3.SuspendLayout();
            this.groupBox4.SuspendLayout();
            this.groupBox5.SuspendLayout();
            this.groupBox6.SuspendLayout();
            this.groupBox7.SuspendLayout();
            this.groupBox8.SuspendLayout();
            this.groupBox9.SuspendLayout();
            this.groupBox10.SuspendLayout();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.FlashOpen);
            this.groupBox1.Controls.Add(this.FlashFileName);
            this.groupBox1.Location = new System.Drawing.Point(8, 8);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(536, 48);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "File to be programed in the Flash";
            // 
            // FlashOpen
            // 
            this.FlashOpen.Location = new System.Drawing.Point(448, 16);
            this.FlashOpen.Name = "FlashOpen";
            this.FlashOpen.Size = new System.Drawing.Size(72, 24);
            this.FlashOpen.TabIndex = 1;
            this.FlashOpen.Text = "Open";
            this.FlashOpen.Click += new System.EventHandler(this.FlashOpen_Click);
            // 
            // FlashFileName
            // 
            this.FlashFileName.Location = new System.Drawing.Point(8, 16);
            this.FlashFileName.Name = "FlashFileName";
            this.FlashFileName.ReadOnly = true;
            this.FlashFileName.Size = new System.Drawing.Size(424, 20);
            this.FlashFileName.TabIndex = 0;
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.EEpromOpen);
            this.groupBox2.Controls.Add(this.EEpromFileName);
            this.groupBox2.Location = new System.Drawing.Point(8, 64);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(536, 48);
            this.groupBox2.TabIndex = 1;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "File to be programed in the EEprom";
            // 
            // EEpromOpen
            // 
            this.EEpromOpen.Location = new System.Drawing.Point(448, 16);
            this.EEpromOpen.Name = "EEpromOpen";
            this.EEpromOpen.Size = new System.Drawing.Size(72, 24);
            this.EEpromOpen.TabIndex = 1;
            this.EEpromOpen.Text = "Open";
            this.EEpromOpen.Click += new System.EventHandler(this.EEpromOpen_Click);
            // 
            // EEpromFileName
            // 
            this.EEpromFileName.Location = new System.Drawing.Point(8, 16);
            this.EEpromFileName.Name = "EEpromFileName";
            this.EEpromFileName.ReadOnly = true;
            this.EEpromFileName.Size = new System.Drawing.Size(424, 20);
            this.EEpromFileName.TabIndex = 0;
            // 
            // groupBox3
            // 
            this.groupBox3.Controls.Add(this.label1);
            this.groupBox3.Controls.Add(this.BLB01);
            this.groupBox3.Controls.Add(this.BLB12);
            this.groupBox3.Controls.Add(this.BLB11);
            this.groupBox3.Controls.Add(this.BLB02);
            this.groupBox3.Location = new System.Drawing.Point(8, 120);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(536, 40);
            this.groupBox3.TabIndex = 2;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "BootLoader Lock Bits to be program";
            // 
            // label1
            // 
            this.label1.ForeColor = System.Drawing.SystemColors.GrayText;
            this.label1.Location = new System.Drawing.Point(296, 16);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(192, 16);
            this.label1.TabIndex = 7;
            this.label1.Text = "Check means programmed (bit = 0)";
            // 
            // BLB01
            // 
            this.BLB01.Location = new System.Drawing.Point(208, 16);
            this.BLB01.Name = "BLB01";
            this.BLB01.Size = new System.Drawing.Size(64, 16);
            this.BLB01.TabIndex = 6;
            this.BLB01.Text = "BLB01";
            this.BLB01.CheckedChanged += new System.EventHandler(this.BLB01_CheckedChanged);
            // 
            // BLB12
            // 
            this.BLB12.Location = new System.Drawing.Point(16, 16);
            this.BLB12.Name = "BLB12";
            this.BLB12.Size = new System.Drawing.Size(64, 16);
            this.BLB12.TabIndex = 3;
            this.BLB12.Text = "BLB12";
            this.BLB12.CheckedChanged += new System.EventHandler(this.BLB12_CheckedChanged);
            // 
            // BLB11
            // 
            this.BLB11.Location = new System.Drawing.Point(80, 16);
            this.BLB11.Name = "BLB11";
            this.BLB11.Size = new System.Drawing.Size(64, 16);
            this.BLB11.TabIndex = 4;
            this.BLB11.Text = "BLB11";
            this.BLB11.CheckedChanged += new System.EventHandler(this.BLB11_CheckedChanged);
            // 
            // BLB02
            // 
            this.BLB02.Location = new System.Drawing.Point(144, 16);
            this.BLB02.Name = "BLB02";
            this.BLB02.Size = new System.Drawing.Size(64, 16);
            this.BLB02.TabIndex = 5;
            this.BLB02.Text = "BLB02";
            this.BLB02.CheckedChanged += new System.EventHandler(this.BLB02_CheckedChanged);
            // 
            // groupBox4
            // 
            this.groupBox4.Controls.Add(this.PortOpenClose);
            this.groupBox4.Controls.Add(this.RTS);
            this.groupBox4.Controls.Add(this.DTR);
            this.groupBox4.Controls.Add(this.label3);
            this.groupBox4.Controls.Add(this.label2);
            this.groupBox4.Controls.Add(this.CommSpeed);
            this.groupBox4.Controls.Add(this.PortSelect);
            this.groupBox4.Location = new System.Drawing.Point(8, 168);
            this.groupBox4.Name = "groupBox4";
            this.groupBox4.Size = new System.Drawing.Size(168, 144);
            this.groupBox4.TabIndex = 3;
            this.groupBox4.TabStop = false;
            this.groupBox4.Text = "Comm Setup";
            // 
            // PortOpenClose
            // 
            this.PortOpenClose.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.PortOpenClose.Location = new System.Drawing.Point(32, 112);
            this.PortOpenClose.Name = "PortOpenClose";
            this.PortOpenClose.Size = new System.Drawing.Size(104, 24);
            this.PortOpenClose.TabIndex = 12;
            this.PortOpenClose.Text = "Close Port";
            this.PortOpenClose.Click += new System.EventHandler(this.PortOpenClose_Click);
            // 
            // RTS
            // 
            this.RTS.Location = new System.Drawing.Point(88, 80);
            this.RTS.Name = "RTS";
            this.RTS.Size = new System.Drawing.Size(72, 16);
            this.RTS.TabIndex = 5;
            this.RTS.Text = "RTS";
            this.RTS.CheckedChanged += new System.EventHandler(this.RTS_CheckedChanged);
            // 
            // DTR
            // 
            this.DTR.Location = new System.Drawing.Point(8, 80);
            this.DTR.Name = "DTR";
            this.DTR.Size = new System.Drawing.Size(72, 16);
            this.DTR.TabIndex = 4;
            this.DTR.Text = "DTR";
            this.DTR.CheckedChanged += new System.EventHandler(this.DTR_CheckedChanged);
            // 
            // label3
            // 
            this.label3.Location = new System.Drawing.Point(88, 24);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(64, 16);
            this.label3.TabIndex = 3;
            this.label3.Text = "Speed";
            // 
            // label2
            // 
            this.label2.Location = new System.Drawing.Point(8, 24);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(64, 16);
            this.label2.TabIndex = 2;
            this.label2.Text = "CommPort";
            // 
            // CommSpeed
            // 
            this.CommSpeed.Items.AddRange(new object[] {
            "1200bps",
            "2400bps",
            "4800bps",
            "9600bps",
            "19200bps",
            "38400bps",
            "57600bps",
            "115kbps"});
            this.CommSpeed.Location = new System.Drawing.Point(88, 40);
            this.CommSpeed.Name = "CommSpeed";
            this.CommSpeed.Size = new System.Drawing.Size(72, 21);
            this.CommSpeed.TabIndex = 1;
            this.CommSpeed.Text = "9600";
            this.CommSpeed.SelectionChangeCommitted += new System.EventHandler(this.CommSpeed_SelectionChangeCommitted);
            // 
            // PortSelect
            // 
            this.PortSelect.ImeMode = System.Windows.Forms.ImeMode.Off;
            this.PortSelect.Items.AddRange(new object[] {
            "ComPort",
            "Com1",
            "Com2",
            "Com3",
            "Com4",
            "Com5",
            "Com6",
            "Com7",
            "Com8",
            "Com9",
            "Com10",
            "Com11",
            "Com12",
            "Com13",
            "Com14",
            "Com15",
            "Com16",
            "Com17",
            "Com18",
            "Com19"});
            this.PortSelect.Location = new System.Drawing.Point(8, 40);
            this.PortSelect.Name = "PortSelect";
            this.PortSelect.Size = new System.Drawing.Size(72, 21);
            this.PortSelect.TabIndex = 0;
            this.PortSelect.Text = "ComPort";
            this.PortSelect.SelectionChangeCommitted += new System.EventHandler(this.PortSelect_SelectionChangeCommitted);
            // 
            // MonitorButton
            // 
            this.MonitorButton.Location = new System.Drawing.Point(88, 24);
            this.MonitorButton.Name = "MonitorButton";
            this.MonitorButton.Size = new System.Drawing.Size(72, 24);
            this.MonitorButton.TabIndex = 11;
            this.MonitorButton.Text = "Monitor";
            this.MonitorButton.Click += new System.EventHandler(this.Monitor_Click);
            // 
            // SendReset
            // 
            this.SendReset.Location = new System.Drawing.Point(8, 24);
            this.SendReset.Name = "SendReset";
            this.SendReset.Size = new System.Drawing.Size(72, 24);
            this.SendReset.TabIndex = 10;
            this.SendReset.Text = "Send Reset";
            this.SendReset.Click += new System.EventHandler(this.SendReset_Click);
            // 
            // groupBox5
            // 
            this.groupBox5.Controls.Add(this.Device);
            this.groupBox5.Controls.Add(this.label8);
            this.groupBox5.Controls.Add(this.label7);
            this.groupBox5.Controls.Add(this.label6);
            this.groupBox5.Controls.Add(this.label5);
            this.groupBox5.Controls.Add(this.label4);
            this.groupBox5.Controls.Add(this.PageSize);
            this.groupBox5.Controls.Add(this.BootSize);
            this.groupBox5.Controls.Add(this.FlashSize);
            this.groupBox5.Controls.Add(this.EEpromSize);
            this.groupBox5.Location = new System.Drawing.Point(184, 168);
            this.groupBox5.Name = "groupBox5";
            this.groupBox5.Size = new System.Drawing.Size(160, 144);
            this.groupBox5.TabIndex = 4;
            this.groupBox5.TabStop = false;
            this.groupBox5.Text = "Target";
            // 
            // Device
            // 
            this.Device.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.Device.ForeColor = System.Drawing.SystemColors.ActiveCaption;
            this.Device.Location = new System.Drawing.Point(80, 24);
            this.Device.Name = "Device";
            this.Device.Size = new System.Drawing.Size(74, 16);
            this.Device.TabIndex = 5;
            this.Device.Text = "xxxx";
            // 
            // label8
            // 
            this.label8.Location = new System.Drawing.Point(8, 120);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(72, 16);
            this.label8.TabIndex = 4;
            this.label8.Text = "EEpromSize:";
            // 
            // label7
            // 
            this.label7.Location = new System.Drawing.Point(8, 96);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(72, 16);
            this.label7.TabIndex = 3;
            this.label7.Text = "Flash Size:";
            // 
            // label6
            // 
            this.label6.Location = new System.Drawing.Point(8, 72);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(72, 16);
            this.label6.TabIndex = 2;
            this.label6.Text = "BootSize:";
            // 
            // label5
            // 
            this.label5.Location = new System.Drawing.Point(8, 48);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(72, 16);
            this.label5.TabIndex = 1;
            this.label5.Text = "PageSize:";
            // 
            // label4
            // 
            this.label4.Location = new System.Drawing.Point(8, 24);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(72, 16);
            this.label4.TabIndex = 0;
            this.label4.Text = "Device:";
            // 
            // PageSize
            // 
            this.PageSize.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.PageSize.ForeColor = System.Drawing.SystemColors.ActiveCaption;
            this.PageSize.Location = new System.Drawing.Point(80, 48);
            this.PageSize.Name = "PageSize";
            this.PageSize.Size = new System.Drawing.Size(72, 16);
            this.PageSize.TabIndex = 6;
            this.PageSize.Text = "xxxx";
            // 
            // BootSize
            // 
            this.BootSize.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.BootSize.ForeColor = System.Drawing.SystemColors.ActiveCaption;
            this.BootSize.Location = new System.Drawing.Point(80, 72);
            this.BootSize.Name = "BootSize";
            this.BootSize.Size = new System.Drawing.Size(72, 16);
            this.BootSize.TabIndex = 7;
            this.BootSize.Text = "xxxx";
            // 
            // FlashSize
            // 
            this.FlashSize.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.FlashSize.ForeColor = System.Drawing.SystemColors.ActiveCaption;
            this.FlashSize.Location = new System.Drawing.Point(80, 96);
            this.FlashSize.Name = "FlashSize";
            this.FlashSize.Size = new System.Drawing.Size(72, 16);
            this.FlashSize.TabIndex = 8;
            this.FlashSize.Text = "xxxx";
            // 
            // EEpromSize
            // 
            this.EEpromSize.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.EEpromSize.ForeColor = System.Drawing.SystemColors.ActiveCaption;
            this.EEpromSize.Location = new System.Drawing.Point(80, 120);
            this.EEpromSize.Name = "EEpromSize";
            this.EEpromSize.Size = new System.Drawing.Size(72, 16);
            this.EEpromSize.TabIndex = 9;
            this.EEpromSize.Text = "xxxx";
            // 
            // groupBox6
            // 
            this.groupBox6.Controls.Add(this.MessageList);
            this.groupBox6.Location = new System.Drawing.Point(352, 168);
            this.groupBox6.Name = "groupBox6";
            this.groupBox6.Size = new System.Drawing.Size(192, 384);
            this.groupBox6.TabIndex = 5;
            this.groupBox6.TabStop = false;
            this.groupBox6.Text = "Messages";
            // 
            // MessageList
            // 
            this.MessageList.Location = new System.Drawing.Point(8, 16);
            this.MessageList.Name = "MessageList";
            this.MessageList.Size = new System.Drawing.Size(176, 355);
            this.MessageList.TabIndex = 0;
            // 
            // groupBox7
            // 
            this.groupBox7.Controls.Add(this.Status);
            this.groupBox7.Location = new System.Drawing.Point(8, 448);
            this.groupBox7.Name = "groupBox7";
            this.groupBox7.Size = new System.Drawing.Size(336, 48);
            this.groupBox7.TabIndex = 6;
            this.groupBox7.TabStop = false;
            this.groupBox7.Text = "Status";
            // 
            // Status
            // 
            this.Status.Location = new System.Drawing.Point(8, 16);
            this.Status.Name = "Status";
            this.Status.Size = new System.Drawing.Size(320, 20);
            this.Status.TabIndex = 0;
            // 
            // groupBox8
            // 
            this.groupBox8.Controls.Add(this.ProgressBar);
            this.groupBox8.Location = new System.Drawing.Point(8, 504);
            this.groupBox8.Name = "groupBox8";
            this.groupBox8.Size = new System.Drawing.Size(336, 48);
            this.groupBox8.TabIndex = 7;
            this.groupBox8.TabStop = false;
            this.groupBox8.Text = "Progress";
            // 
            // ProgressBar
            // 
            this.ProgressBar.Location = new System.Drawing.Point(8, 16);
            this.ProgressBar.Name = "ProgressBar";
            this.ProgressBar.Size = new System.Drawing.Size(320, 24);
            this.ProgressBar.TabIndex = 0;
            // 
            // groupBox9
            // 
            this.groupBox9.Controls.Add(this.listBox1);
            this.groupBox9.Location = new System.Drawing.Point(184, 320);
            this.groupBox9.Name = "groupBox9";
            this.groupBox9.Size = new System.Drawing.Size(160, 120);
            this.groupBox9.TabIndex = 8;
            this.groupBox9.TabStop = false;
            this.groupBox9.Text = "Thanks to read";
            // 
            // listBox1
            // 
            this.listBox1.Items.AddRange(new object[] {
            "Hi AVR Fans!",
            "",
            "  The source code for ",
            "the windows ",
            "application is ",
            "available for US$100 ",
            "and is subject to a ",
            "Non Disclosure ",
            "Agreement.",
            "",
            "The code is written",
            " in Microsoft Visual C# ",
            ".NET Studio 2.0. ",
            "",
            "  If you find this software ",
            "useful, I would be ",
            "pleased to receive some ",
            "Atmel Megas\' of your",
            "choice. ",
            "",
            "I have spent a lot of ",
            "time and effort to ",
            "write this software ",
            "and would really",
            "appreciate it is ",
            "you took a a few ",
            "minutes to put a",
            " \'Mega\' in the mail ",
            "to me.",
            "",
            "If you\'d rather not",
            "send parts via mail",
            "would be ",
            "pleased to receive",
            "some $ of your ",
            "choice to my paypal ",
            "account:",
            "bibi@microsyl.com",
            "",
            "Thanks you for using",
            " Megaload.",
            "",
            "Yours faithfully,",
            "",
            "Sylvain Bissonnette",
            "660 Marco-Polo",
            "Boucherville, Qc",
            "J4B 5R4",
            "CANADA"});
            this.listBox1.Location = new System.Drawing.Point(8, 16);
            this.listBox1.Name = "listBox1";
            this.listBox1.Size = new System.Drawing.Size(144, 95);
            this.listBox1.TabIndex = 0;
            // 
            // groupBox10
            // 
            this.groupBox10.Controls.Add(this.AboutButton);
            this.groupBox10.Controls.Add(this.SendReset);
            this.groupBox10.Controls.Add(this.MonitorButton);
            this.groupBox10.Location = new System.Drawing.Point(8, 320);
            this.groupBox10.Name = "groupBox10";
            this.groupBox10.Size = new System.Drawing.Size(168, 120);
            this.groupBox10.TabIndex = 12;
            this.groupBox10.TabStop = false;
            this.groupBox10.Text = "Command";
            // 
            // AboutButton
            // 
            this.AboutButton.Location = new System.Drawing.Point(8, 80);
            this.AboutButton.Name = "AboutButton";
            this.AboutButton.Size = new System.Drawing.Size(152, 24);
            this.AboutButton.TabIndex = 12;
            this.AboutButton.Text = "About";
            this.AboutButton.Click += new System.EventHandler(this.AboutBoutton_Click);
            // 
            // Comm1
            // 
            this.Comm1.DataReceived += new System.IO.Ports.SerialDataReceivedEventHandler(this.Comm1_DataReceived);
            // 
            // MegaLoadForm
            // 
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(554, 560);
            this.Controls.Add(this.groupBox10);
            this.Controls.Add(this.groupBox9);
            this.Controls.Add(this.groupBox8);
            this.Controls.Add(this.groupBox7);
            this.Controls.Add(this.groupBox6);
            this.Controls.Add(this.groupBox5);
            this.Controls.Add(this.groupBox4);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.Name = "MegaLoadForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "MegaLoad .NET V:7.1";
            this.Load += new System.EventHandler(this.MegaLoadForm_Load);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.groupBox3.ResumeLayout(false);
            this.groupBox4.ResumeLayout(false);
            this.groupBox5.ResumeLayout(false);
            this.groupBox6.ResumeLayout(false);
            this.groupBox7.ResumeLayout(false);
            this.groupBox7.PerformLayout();
            this.groupBox8.ResumeLayout(false);
            this.groupBox9.ResumeLayout(false);
            this.groupBox10.ResumeLayout(false);
            this.ResumeLayout(false);

		}
		#endregion

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main() 
		{
			Application.Run(new MegaLoadForm());
		}

		private bool CommSetup(int Port,int Speed)
		{
			Comm1.Close();
            if (Port == 1) Comm1.PortName = "COM1";
            if (Port == 2) Comm1.PortName = "COM2";
            if (Port == 3) Comm1.PortName = "COM3";
            if (Port == 4) Comm1.PortName = "COM4";
            if (Port == 5) Comm1.PortName = "COM5";
            if (Port == 6) Comm1.PortName = "COM6";
            if (Port == 7) Comm1.PortName = "COM7";
            if (Port == 8) Comm1.PortName = "COM8";
            if (Port == 9) Comm1.PortName = "COM9";
            if (Port == 10) Comm1.PortName = "COM10";
            if (Port == 11) Comm1.PortName = "COM11";
            if (Port == 12) Comm1.PortName = "COM12";
            if (Port == 13) Comm1.PortName = "COM13";
            if (Port == 14) Comm1.PortName = "COM14";
            if (Port == 15) Comm1.PortName = "COM15";
            if (Port == 16) Comm1.PortName = "COM16";
            if (Port == 17) Comm1.PortName = "COM17";
            if (Port == 18) Comm1.PortName = "COM18";
            if (Port == 19) Comm1.PortName = "COM19";
            if (Port == 20) Comm1.PortName = "COM20";

            if (Speed == 0) Comm1.BaudRate = 1200;
            if (Speed == 1) Comm1.BaudRate = 2400;
            if (Speed == 2) Comm1.BaudRate = 4800;
            if (Speed == 3) Comm1.BaudRate = 9600;
            if (Speed == 4) Comm1.BaudRate = 19200;
            if (Speed == 5) Comm1.BaudRate = 38400;
            if (Speed == 6) Comm1.BaudRate = 57600;
            if (Speed == 7) Comm1.BaudRate = 115200;
			try
			{
                Comm1.Open();

                if (DTR.Checked) Comm1.DtrEnable = true;
                else Comm1.DtrEnable = false;

                if (RTS.Checked) Comm1.RtsEnable = true;
                else Comm1.RtsEnable = false;

				return true;
			}
			catch
			{
				return false;
			}
		}

		private byte ASCIItoHEX(char ch)
		{
			if ((ch >= '0') && (ch <= '9')) return (byte)((byte)ch - (byte)'0');
			if ((ch >= 'A') && (ch <= 'F')) return (byte)((byte)ch - (byte)'A' + 10);
			if ((ch >= 'a') && (ch <= 'f')) return (byte)((byte)ch - (byte)'a' + 10);
			return 0;
		}
        //*********************************************************************************
        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        delegate void SetMessageListCallback(string Message);
		private void SendMessage(string Message)
		{
			int i = MessageList.Items.Count;
            if (this.MessageList.InvokeRequired)
            {
                SetMessageListCallback d = new SetMessageListCallback(SendMessage);
                this.Invoke(d, new object[] { Message });
            }
            else
            {
                if (Message.Length == 0)
                    MessageList.Items.Clear();
                else
                {
                    MessageList.Items.Insert(i, Message);
                    MessageList.SelectedIndex = i;
                }
            }
		}

        //*********************************************************************************
        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        delegate void SetStatusTextCallback(string Message);
        private void SetStatusText(string Message)
        {
            if (this.Status.InvokeRequired)
            {
                SetStatusTextCallback d = new SetStatusTextCallback(SetStatusText);
                this.Invoke(d, new object[] { Message });
            }
            else
            {
                Status.Text = Message;
            }
        }
        //*********************************************************************************
        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        delegate void SetLabelTextCallback(string Message, Label label);
        private void SetLabelText(string Message, Label label)
        {
            if (label.InvokeRequired)
            {
                SetLabelTextCallback d = new SetLabelTextCallback(SetLabelText);
                this.Invoke(d, new object[] { Message, label });
            }
            else
            {
                label.Text = Message;
            }
        }

        //*********************************************************************************
        // This delegate enables asynchronous calls for setting
        // the text property on a TextBox control.
        delegate void SetProgressBarCallback(int max, int val);
        private void SetProgressBar(int max, int val)
        {
            if (this.ProgressBar.InvokeRequired)
            {
                SetProgressBarCallback d = new SetProgressBarCallback(SetProgressBar);
                this.Invoke(d, new object[] { (int)max, (int)val });
            }
            else
            {
			    ProgressBar.Maximum = max;
				ProgressBar.Value = val;
            }
        }

		private bool FillFlash()
		{
			byte MemLocHi;
			byte MemLocLo;
			int	 MemLoc;
			int  MemOffset;
			byte QteData;
			byte LocalCheckSum;
			byte CheckSum;
			byte RecType;
			int Li;
			int Ptr = 0;
			int MemUsage;
			char[] Buf = new char[512];
			int i;
			StreamReader Stream;
			string Line = "";

			try
			{
				Stream = new StreamReader(new FileStream(FlashFileNameHex,FileMode.Open,FileAccess.Read));
			}
			catch
			{
				return false;
			}

			if (Stream != System.IO.StreamReader.Null)
			{
				SendMessage("Open Flash Hex File");
				Li = 0;
				FlashMin = 0xffff;
				FlashMax = 0x0000;
				MemOffset = 0;

				for (i=0;i<(256*1024);i++) Flash[i] = 0xff;
				while (true)
				{
					LocalCheckSum = 0;
					Ptr = 0;

					Line = Stream.ReadLine();
					if (Line == null) break;
					Buf = Line.ToCharArray();
					if (Buf[Ptr++] != ':')
					{
						SendMessage("Error in Flash Hex File Line " + Li.ToString());
						return false;
					}

					QteData = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
					LocalCheckSum += QteData;

					MemLocHi = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
					LocalCheckSum += MemLocHi;

					MemLocLo = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
					LocalCheckSum += MemLocLo;

					RecType = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
					LocalCheckSum += RecType;

					MemLoc = (MemLocHi << 8) + MemLocLo;

					if (RecType == 0) // data
					{
						if (FlashMin > (MemOffset + MemLoc)) FlashMin = (MemOffset + MemLoc);
						for (i=0;i<QteData;i++)
						{
							Flash[MemOffset + MemLoc + i] = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
							LocalCheckSum += Flash[MemOffset + MemLoc + i];
							if (FlashMax < (MemOffset + MemLoc + i)) FlashMax = (MemOffset + MemLoc + i);
						}
					}

					if (RecType == 1) // eof
					{
						MemUsage = FlashMax - FlashMin + 1;
						SendMessage("Flash Hex File OK " + MemUsage.ToString() + " Bytes");
						Stream.Close();
						return true;
					}

					if (RecType == 2) // Extended Segment Address Record
					{
						MemLocHi = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += MemLocHi;

						MemLocLo = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += MemLocLo;

						MemOffset = ((MemLocHi << 8) + MemLocLo);
						MemOffset = (MemOffset << 4);
					}

					if (RecType == 4) // Extended Linear Address Record
					{
						MemLocHi = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += MemLocHi;

						MemLocLo = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += MemLocLo;

						MemOffset = ((MemLocHi << 8) + MemLocLo);
						MemOffset = (MemOffset << 16);
					}

					CheckSum = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
					LocalCheckSum = (byte)(0x0100 - LocalCheckSum);
					if (CheckSum != LocalCheckSum)
					{
						SendMessage("Error in Flash Hex File " + Li.ToString());
						Stream.Close();
						return false;
					}
					Li++;
				}
				MemUsage = FlashMax - FlashMin + 1;
				SendMessage("Flash Hex File OK " + MemUsage.ToString() + " Bytes");
				Stream.Close();
				return true;
			}
			return false;
		}

		private void SendFlashPage()
		{
			int ByteSend = 0;
			byte CheckSum = 0;;

			if ((PagePtr*PageSizeInt) > FlashMax) // All data ahd been send
			{
				CommWriteByte(0xff);
				CommWriteByte(0xff);

				SendMessage("Flash Prog Done!");
				if (Retry == 0) SetStatusText("Succesful finished, Waiting for next target");
				else SetStatusText("Succesful finished with retrys, Waiting for next target");
                SetProgressBar(1, 0);

				PagePtr = 0;
			}
			else
			{
				CommWriteByte((byte)((PagePtr>>8) & 0x00ff));
				CommWriteByte((byte)((PagePtr & 0x00ff)));

				CheckSum = 0;

                Comm1.Write(Flash, (PagePtr * PageSizeInt), PageSizeInt);
				while(ByteSend < PageSizeInt)
				{
					CheckSum += Flash[(PagePtr*PageSizeInt)+ByteSend];
					ByteSend++;
				}
				CommWriteByte((byte)CheckSum);
				SendMessage("Sending Page #" + PagePtr.ToString());

				if (PageSizeInt > 0)
				{
                    SetProgressBar(FlashMax / PageSizeInt, PagePtr);
				}
			}
		}

		private bool FillEEprom()
		{
			byte MemLocHi;
			byte MemLocLo;
			int	 MemLoc;
			int  MemOffset;
			byte QteData;
			byte LocalCheckSum;
			byte CheckSum;
			byte RecType;
			int Li;
			int Ptr = 0;
			int MemUsage;
			char[] Buf = new char[512];
			int i;
			StreamReader Stream;
			string Line = "";

			try
			{
				Stream = new StreamReader(new FileStream(EEpromFileNameHex,FileMode.Open,FileAccess.Read));
			}
			catch
			{
				return false;
			}

			if (Stream != System.IO.StreamReader.Null)
			{
				SendMessage("Open EEprom Hex File");
				Li = 0;
				EEpromMin = 0xffff;
				EEpromMax = 0x0000;
				MemOffset = 0;

				for (i=0;i<(128*1024);i++) 
				{
					EEprom[i] = 0xff;
					EEpromUse[i] = false;
				}
					while (true)
					{
						LocalCheckSum = 0;
						Ptr = 0;

						Line = Stream.ReadLine();
						if (Line == null) break;
						Buf = Line.ToCharArray();
						if (Buf[Ptr++] != ':')
						{
							SendMessage("Error in EEprom Hex File Line " + Li.ToString());
							return false;
						}

						QteData = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += QteData;

						MemLocHi = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += MemLocHi;

						MemLocLo = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += MemLocLo;

						RecType = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum += RecType;

						MemLoc = (MemLocHi << 8) + MemLocLo;

						if (MemLoc > 4096)
						{
							SendMessage("EEprom Hex File Out Of Range");
							Stream.Close();
							return false;
						}

						if (RecType == 0) // data
						{
							if (EEpromMin > (MemOffset + MemLoc)) EEpromMin = (MemOffset + MemLoc);
							for (i=0;i<QteData;i++)
							{
								EEprom[MemOffset + MemLoc + i] = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
								EEpromUse[MemOffset + MemLoc + i] = true;
								LocalCheckSum += EEprom[MemOffset + MemLoc + i];
								if (EEpromMax < (MemOffset + MemLoc + i)) EEpromMax = (MemOffset + MemLoc + i);
							}
						}

						if (RecType == 1) // eof
						{
							MemUsage = EEpromMax - EEpromMin + 1;
							SendMessage("EEprom Hex File OK " + MemUsage.ToString() + " Bytes");
							Stream.Close();
							return true;
						}

						if (RecType == 2) // Extended Segment Address Record
						{
							MemLocHi = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
							LocalCheckSum += MemLocHi;

							MemLocLo = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
							LocalCheckSum += MemLocLo;

							MemOffset = ((MemLocHi << 8) + MemLocLo);
							MemOffset = (MemOffset << 4);
						}

						if (RecType == 4) // Extended Linear Address Record
						{
							MemLocHi = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
							LocalCheckSum += MemLocHi;

							MemLocLo = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
							LocalCheckSum += MemLocLo;

							MemOffset = ((MemLocHi << 8) + MemLocLo);
							MemOffset = (MemOffset << 16);
						}

						CheckSum = (byte)((ASCIItoHEX(Buf[Ptr++]) << 4) + ASCIItoHEX(Buf[Ptr++]));
						LocalCheckSum = (byte)(0x0100 - LocalCheckSum);
						if (CheckSum != LocalCheckSum)
						{
							SendMessage("Error in EEprom Hex File " + Li.ToString());
							Stream.Close();
							return false;
						}
						Li++;
					}
					MemUsage = EEpromMax - EEpromMin + 1;
					SendMessage("EEprom Hex File OK " + MemUsage.ToString() + " Bytes");
					Stream.Close();
					return true;
				}
				return false;
			}

		private void SendEEpromByte()
		{
			byte CheckSum = 0;;


			if (BytePtr > EEpromMax) // All data had been send
			{
				CommWriteByte(0xff);
				CommWriteByte(0xff);
				SendMessage("EEprom Prog Done!");
				if (Retry == 0) SetStatusText("Succesful finished, Waiting for next target");
				else SetStatusText("Succesful finished with retrys, Waiting for next target");
				ProgressBar.Value = 0;

				BytePtr = 0;
			}				
			else
			{
				while((EEpromUse[BytePtr] != true) && (BytePtr < 4094)) BytePtr++;

				CommWriteByte((byte)((BytePtr>>8) & 0x00ff));
				CommWriteByte((byte)((BytePtr & 0x00ff)));
				CommWriteByte((byte)EEprom[BytePtr]);

				CheckSum = (byte)((BytePtr>>8) & 0x00ff);
				CheckSum += (byte)(BytePtr & 0x00ff);
				CheckSum += (byte)EEprom[BytePtr];

				CommWriteByte((byte)CheckSum);

				SendMessage("Sending Byte #" + BytePtr.ToString());

				if (EEpromSizeInt > 0)
				{
                    SetProgressBar(EEpromMax, BytePtr);
				}
			}
		}

		private void SendLockBit()
		{
			byte LockBit = 0x00;

			if (BLB01.Checked == true) LockBit += 0x04;
			if (BLB02.Checked == true) LockBit += 0x08;
			if (BLB11.Checked == true) LockBit += 0x10;
			if (BLB12.Checked == true) LockBit += 0x20;

			CommWriteByte((byte)LockBit);
			CommWriteByte((byte)~LockBit);
			SendMessage("Sending LockBits");
		}

		private void FlashOpen_Click(object sender, System.EventArgs e)
		{
			OpenFileDialog.Filter = "HEX File (*.hex)|*.hex|All files (*.*)|*.*";
			OpenFileDialog.ReadOnlyChecked = true;
			OpenFileDialog.ShowDialog(this);
			string FileName = OpenFileDialog.FileName.ToString();
			if (FileName != null)
			{

				FlashFileNameHex = FileName;
				FlashFileName.Text = FileName;
				SendMessage("Flash File Selected");
				SetStatusText("Ready, Waiting for target");
				RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
				if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
				Key.SetValue("FlashFileName",FileName);
			}
		}

		private void EEpromOpen_Click(object sender, System.EventArgs e)
		{
			OpenFileDialog.Filter = "HEX File (*.hex)|*.hex|All files (*.*)|*.*";
			OpenFileDialog.ReadOnlyChecked = true;
			OpenFileDialog.ShowDialog(this);
			string FileName = OpenFileDialog.FileName.ToString();
			if (FileName != null)
			{
				EEpromFileNameHex = FileName;
				EEpromFileName.Text = FileName;
				SendMessage("EEprom File Selected");
				SetStatusText("Ready, Waiting for target");
				RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
				if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
				Key.SetValue("EEpromFileName",FileName);
			}
		
		}

		private void PortSelect_SelectionChangeCommitted(object sender, System.EventArgs e)
		{
			if (PortSelect.SelectedIndex == 0) return;

			if (CommSetup(PortSelect.SelectedIndex,CommSpeed.SelectedIndex))
			{
				RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
				if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
				Key.SetValue("CommPort",PortSelect.SelectedIndex);
			}
			else
			{
				PortSelect.SelectedIndex = 0;
				MessageBox.Show("Invalid Port Selection", "MegaLoad .NET",MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
			}
		}

		private void CommSpeed_SelectionChangeCommitted(object sender, System.EventArgs e)
		{
			if (CommSetup(PortSelect.SelectedIndex,CommSpeed.SelectedIndex))
			{
				RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
				if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
				Key.SetValue("CommSpeed",CommSpeed.SelectedIndex);
			}
			else
			{
				PortSelect.SelectedIndex = 0;
			}
		}

		private void MegaLoadForm_Load(object sender, System.EventArgs e)
		{
			CommSpeed.SelectedIndex = 0;
			Flash = new byte[512 * 1024];
			EEprom = new byte[128 * 1024];
			EEpromUse = new bool[128 * 1024];

			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET");

			if (Key == null)
			{
				FlashFileName.Text = "";
				EEpromFileName.Text = "";
				PortSelect.SelectedIndex = 0;
				CommSpeed.SelectedIndex = 0;
			}

			// Flash File Name
			try
			{
				if (Key.GetValue("FlashFileName") != null)
				{
					FlashFileNameHex = Key.GetValue("FlashFileName").ToString();
					FlashFileName.Text = FlashFileNameHex;
				}
				else FlashFileName.Text = "";
			}
			catch
			{
				FlashFileName.Text = "";
			}

			// EEprom File Name
			try
			{
				if (Key.GetValue("EEpromFileName") != null)
				{
					EEpromFileNameHex = Key.GetValue("EEpromFileName").ToString();
					EEpromFileName.Text = EEpromFileNameHex;
				}
				else EEpromFileName.Text = "";
			}
			catch
			{
				EEpromFileName.Text = "";
			}

			// ComPort
			try
			{
				if (Key.GetValue("CommPort") != null)
				{
					PortSelect.SelectedIndex = System.Convert.ToInt16(Key.GetValue("CommPort").ToString());
				}
				else PortSelect.SelectedIndex = 0;
			}
			catch
			{
				PortSelect.SelectedIndex = 0;
			}


			try
			{
				if (Key.GetValue("CommSpeed") != null)
				{
					CommSpeed.SelectedIndex = System.Convert.ToInt16(Key.GetValue("CommSpeed").ToString());
				}
				else CommSpeed.SelectedIndex = 0;
			}
			catch
			{
				CommSpeed.SelectedIndex = 0;
			}

			try
			{
				if (Key.GetValue("CommDTR") != null)
				{
					DTR.Checked = System.Convert.ToBoolean(Key.GetValue("CommDTR").ToString());
					if (Comm1.IsOpen == true)
					{
                        if (DTR.Checked) Comm1.DtrEnable = true;
                        else Comm1.DtrEnable = false;
					}
				}
			}
			catch
			{

			}

			try
			{
				if (Key.GetValue("CommRTS") != null)
				{
					RTS.Checked = System.Convert.ToBoolean(Key.GetValue("CommRTS").ToString());
                    if (Comm1.IsOpen == true)
					{
                        if (RTS.Checked) Comm1.RtsEnable = true;
                        else Comm1.RtsEnable = false;
					}
				}
			}
			catch
			{

			}

			try
			{
				if (Key.GetValue("BLB12") != null)
				{
					BLB12.Checked = System.Convert.ToBoolean(Key.GetValue("BLB12").ToString());
				}
			}
			catch
			{

			}

			try
			{
				if (Key.GetValue("BLB11") != null)
				{
					BLB11.Checked = System.Convert.ToBoolean(Key.GetValue("BLB11").ToString());
				}
			}
			catch
			{

			}

			try
			{
				if (Key.GetValue("BLB02") != null)
				{
					BLB02.Checked = System.Convert.ToBoolean(Key.GetValue("BLB02").ToString());
				}
			}
			catch
			{

			}

			try
			{
				if (Key.GetValue("BLB01") != null)
				{
					BLB01.Checked = System.Convert.ToBoolean(Key.GetValue("BLB01").ToString());
				}
			}
			catch
			{

			}

			try
			{
				if (!CommSetup(PortSelect.SelectedIndex,CommSpeed.SelectedIndex))
				{
					PortSelect.SelectedIndex = 0;
					CommSpeed.SelectedIndex = 0;
				}
			}
			catch
			{

			}

			SetStatusText("Ready, Waiting for target");

			if (!FillFlash())
			{
				SetStatusText("No Flash File...  Open file first!");
				FlashFileName.Text = "";
			}

			ToolTip.SetToolTip(AboutButton,"Some nice thing to read!");
			ToolTip.SetToolTip(PortOpenClose,"Close & Open PC Comm Port. (this allows other software to use the Comm Port");
			ToolTip.SetToolTip(MonitorButton,"Open terminal screen (useful for debugging)");
			ToolTip.SetToolTip(SendReset,"Send a reset string on the comport, could be use to reset your MCU, string is RESET");
			ToolTip.SetToolTip(DTR,"Toggle the state of DTR (could be used to reset target or provide power for optical RS485 Converter)");
			ToolTip.SetToolTip(RTS,"Toggle the state of RTS (could be used to reset target or provide power for optical RS485 Converter)");
			ToolTip.SetToolTip(Status,"Status of the application");
			ToolTip.SetToolTip(PortSelect,"Select Comm Port to connect to target");
			ToolTip.SetToolTip(ProgressBar,"Display progress indicator of Flash/EEPROM programming");
			ToolTip.SetToolTip(MessageList,"Application Status Information");
			ToolTip.SetToolTip(EEpromSize,"Displays Target's EEPROM size (in bytes)");
			ToolTip.SetToolTip(FlashSize,"Displays Target's FLASH size (in bytes)");
			ToolTip.SetToolTip(BootSize,"Displays Bootloader size (in words)");
			ToolTip.SetToolTip(PageSize,"Display Target's Page Size (in bytes)");
			ToolTip.SetToolTip(Device,"Display Target 's Device Type");
			ToolTip.SetToolTip(CommSpeed,"Select Communication speed");
			ToolTip.SetToolTip(BLB01,"BLB01->Lock bit setting");
			ToolTip.SetToolTip(BLB02,"BLB02->Lock bit setting");
			ToolTip.SetToolTip(BLB11,"BLB11->Lock bit setting");
			ToolTip.SetToolTip(BLB12,"BLB12->Lock bit setting");
			ToolTip.SetToolTip(EEpromOpen,"Open EEPROM data file to be programmed (in HEX format)");
			ToolTip.SetToolTip(FlashOpen,"Open Flash data file to be programmed (in HEX format)");
			ToolTip.SetToolTip(EEpromFileName,"Filename of  EEPROM data file to be programmed on bootload (in HEX format)");
			ToolTip.SetToolTip(FlashFileName,"Filename of Flash data file to be programmed on bootload (in HEX format)");
		}

		private void DTR_CheckedChanged(object sender, System.EventArgs e)
		{
			if (Comm1.IsOpen == true)
			{
                if (DTR.Checked) Comm1.DtrEnable = true;
                else Comm1.DtrEnable = false;
			}

			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
			if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
			Key.SetValue("CommDTR",DTR.Checked);
		}

		private void RTS_CheckedChanged(object sender, System.EventArgs e)
		{
            if (Comm1.IsOpen == true)
			{
                if (RTS.Checked) Comm1.RtsEnable = true;
                else Comm1.RtsEnable = false;
			}

			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
			if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
			Key.SetValue("CommRTS",RTS.Checked);		
		}

		private void BLB12_CheckedChanged(object sender, System.EventArgs e)
		{
			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
			if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
			Key.SetValue("BLB12",BLB12.Checked);	
		
		}

		private void BLB11_CheckedChanged(object sender, System.EventArgs e)
		{
			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
			if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
			Key.SetValue("BLB11",BLB11.Checked);	
		
		}

		private void BLB02_CheckedChanged(object sender, System.EventArgs e)
		{
			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
			if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
			Key.SetValue("BLB02",BLB02.Checked);	
		
		}

		private void BLB01_CheckedChanged(object sender, System.EventArgs e)
		{
			RegistryKey Key = Registry.CurrentUser.OpenSubKey("Software\\MicroSyl\\MegaLoad .NET",true);
			if (Key == null) Key = Registry.CurrentUser.CreateSubKey("Software\\MicroSyl\\MegaLoad .NET");
			Key.SetValue("BLB01",BLB01.Checked);	
		
		}

		private void SendReset_Click(object sender, System.EventArgs e)
		{
			if (Comm1.IsOpen == true )
			{
                Comm1.Write("RESET");
				SendMessage("Reset has been send");
			}
			else
			{
				SendMessage("Your comm is close!");
			}

		}

        private byte[] ByteToByteArray(byte val)
        {
            byte[] array = new byte[1];
            array[0] = val;
            return (array);
        }

        private void CommWriteByte(byte val){
            Comm1.Write(ByteToByteArray(val),0,1);
        }

        private void CommWriteChar(char val)
        {
            Comm1.Write(ByteToByteArray((byte)val), 0, 1);
        }

        private void Comm1_DataReceived(object sender, System.IO.Ports.SerialDataReceivedEventArgs e)
		{
			char ch;

            try
            {
                while (Comm1.BytesToRead > 0)
                {
                    ch = (char)Comm1.ReadByte();

                    if (Monitor != null) Monitor.AddChar(ch);

                    // Auto find OSCCAL/BaudRate can be use as echo
                    if (ch == 0x55) CommWriteByte(0x55);

                    // For in BootLoad mode
                    // Flash prog code
                    if (ch == '>')
                    {
                        CommWriteChar('<');
                        MemType = 'F';
                        SendMessage("");
                        Retry = 0;

                        if (FillFlash())
                        {
                            PagePtr = 0;
                            Retry = 0;
                            SetStatusText("Programming flash.... please wait");
                        }
                        else
                        {
                            SetStatusText("No Flash File... Open file first!");
                            MemType = 'E';
                            CommWriteByte(0xff);
                            CommWriteByte(0xff);
                            Comm1.ReadChar();
                        }
                    }


                    if ((ch == '!') && (MemType == 'F'))
                    {
                        SendFlashPage();
                        PagePtr++;
                    }

                    if ((ch == '@') && (MemType == 'F'))
                    {
                        PagePtr--;
                        Retry++;
                        SendFlashPage();
                        PagePtr++;
                    }

                    // EEprom prog code
                    if (ch == ')')
                    {
                        MemType = 'E';
                        Retry = 0;

                        if (FillEEprom())
                        {
                            BytePtr = 0;
                            Retry = 0;
                            SetStatusText("Programming eeprom.... please wait");
                        }
                        else
                        {
                            SetStatusText("No eeprom File... Open file first!");
                            MemType = 'F';
                            CommWriteByte(0xff);
                            CommWriteByte(0xff);
                            Comm1.ReadChar();
                        }
                    }

                    if ((ch == '!') && (MemType == 'E'))
                    {
                        SendEEpromByte();
                        BytePtr++;
                    }

                    if ((ch == '@') && (MemType == 'E'))
                    {
                        BytePtr--;
                        Retry++;
                        SendEEpromByte();
                        BytePtr++;
                    }

                    if (ch == '%') SendLockBit();

                    if (Retry > 3)
                    {
                        SendMessage("Programming Fail");
                        SetStatusText("Error occured aborted...");
                        while (Comm1.BytesToRead > 0)
                        {
                            CommWriteByte(0xff);
                            CommWriteByte(0xff);
                            Comm1.ReadChar();
                        }
                    }

                    // A-P for device ID
                    if (ch == 'A')
                    {
                        SetLabelText("Mega 8", Device);
                    }
                    if (ch == 'B')
                    {
                        SetLabelText("Mega 16", Device);
                    }
                    if (ch == 'C')
                    {
                        SetLabelText("Mega 64", Device);
                    }
                    if (ch == 'D')
                    {
                        SetLabelText("Mega 128", Device);
                    }
                    if (ch == 'E')
                    {
                        SetLabelText("Mega 32", Device);
                    }
                    if (ch == 'F')
                    {
                        SetLabelText("Mega 162", Device);
                    }
                    if (ch == 'G')
                    {
                        SetLabelText("Mega 169", Device);
                    }
                    if (ch == 'H')
                    {
                        SetLabelText("Mega8515", Device);
                    }
                    if (ch == 'I')
                    {
                        SetLabelText("Mega8535", Device);
                    }
                    if (ch == 'J')
                    {
                        SetLabelText("Mega163", Device);
                    }
                    if (ch == 'K')
                    {
                        SetLabelText("Mega323", Device);
                    }
                    if (ch == 'L')
                    {
                        SetLabelText("Mega48", Device);
                    }
                    if (ch == 'M')
                    {
                        SetLabelText("Mega88", Device);
                    }
                    if (ch == 'N')
                    {
                        SetLabelText("Mega168", Device);
                    }
                    if (ch == 0x80)
                    {
                        SetLabelText("Mega165", Device);
                    }
                    if (ch == 0x81)
                    {
                        SetLabelText("Mega3250", Device);
                    }
                    if (ch == 0x82)
                    {
                        SetLabelText("Mega6450", Device);
                    }
                    if (ch == 0x83)
                    {
                        SetLabelText("Mega3290", Device);
                    }
                    if (ch == 0x84)
                    {
                        SetLabelText("Mega6490", Device);
                    }
                    if (ch == 0x85)
                    {
                        SetLabelText("Mega406", Device);
                    }
                    if (ch == 0x86)
                    {
                        SetLabelText("Mega640", Device);
                    }
                    if (ch == 0x87)
                    {
                        SetLabelText("Mega1280", Device);
                    }
                    if (ch == 0x88)
                    {
                        SetLabelText("Mega2560", Device);
                    }
                    if (ch == 0x89)
                    {
                        SetLabelText("MCAN128", Device);
                    }
                    //---------------------------------------------------------
                    if (ch == 0x8a)
                    {
                        SetLabelText("Mega164", Device);
                    }

                    if (ch == 0x8b)
                    {
                        SetLabelText("Mega328", Device);
                    }

                    if (ch == 0x8c)
                    {
                        SetLabelText("Mega324", Device);
                    }

                    if (ch == 0x8d)
                    {
                        SetLabelText("Mega325", Device);
                    }

                    if (ch == 0x8e)
                    {
                        SetLabelText("Mega644", Device);
                    }

                    if (ch == 0x8f)
                    {
                        SetLabelText("Mega645", Device);
                    }

                    if (ch == 0x90)
                    {
                        SetLabelText("Mega1281", Device);
                    }

                    if (ch == 0x91)
                    {
                        SetLabelText("Mega2561", Device);
                    }

                    if (ch == 0x92)
                    {
                        SetLabelText("Mega2560", Device);
                    }

                    if (ch == 0x93)
                    {
                        SetLabelText("Mega404", Device);
                    }

                    if (ch == 0x94)
                    {
                        SetLabelText("MUSB1286", Device);
                    }

                    if (ch == 0x95)
                    {
                        SetLabelText("MUSB1287", Device);
                    }

                    if (ch == 0x96)
                    {
                        SetLabelText("MUSB162", Device);
                    }

                    if (ch == 0x97)
                    {
                        SetLabelText("MUSB646", Device);
                    }

                    if (ch == 0x98)
                    {
                        SetLabelText("MUSB647", Device);
                    }

                    if (ch == 0x99)
                    {
                        SetLabelText("MUSB82", Device);
                    }

                    if (ch == 0x9a)
                    {
                        SetLabelText("MCAN32", Device);
                    }


                    if (ch == 0x9b)
                    {
                        SetLabelText("MCAN64", Device);
                    }


                    if (ch == 0x9c)
                    {
                        SetLabelText("Mega329", Device);
                    }


                    if (ch == 0x9d)
                    {
                        SetLabelText("Mega649", Device);
                    }

                    if (ch == 0x9e)
                    {
                        SetLabelText("Mega256", Device);
                    }


                    // Q-Z for PageSize
                    if (ch == 'Q')
                    {
                        SetLabelText("32 Bytes", PageSize);
                        PageSizeInt = 32;
                    }
                    if (ch == 'R')
                    {
                        SetLabelText("64 Bytes", PageSize);
                        PageSizeInt = 64;
                    }
                    if (ch == 'S')
                    {
                        SetLabelText("128 Bytes", PageSize);
                        PageSizeInt = 128;
                    }
                    if (ch == 'T')
                    {
                        SetLabelText("256 Bytes", PageSize);
                        PageSizeInt = 256;
                    }
                    if (ch == 'V')
                    {
                        SetLabelText("512 Bytes", PageSize);
                        PageSizeInt = 512;
                    }

                    // a-k for BootSize
                    if (ch == 'a')
                    {
                        SetLabelText("128 Words", BootSize);
                        BootSizeInt = 128;
                    }
                    if (ch == 'b')
                    {
                        SetLabelText("256 Words", BootSize);
                        BootSizeInt = 256;
                    }
                    if (ch == 'c')
                    {
                        SetLabelText("512 Words", BootSize);
                        BootSizeInt = 512;
                    }
                    if (ch == 'd')
                    {
                        SetLabelText("1k Words", BootSize);
                        BootSizeInt = 1024;
                    }
                    if (ch == 'e')
                    {
                        SetLabelText("2k Words", BootSize);
                        BootSizeInt = 2048;
                    }
                    if (ch == 'f')
                    {
                        SetLabelText("4k Words", BootSize);
                        BootSizeInt = 4096;
                    }


                    // l-u for FlashSize
                    if (ch == 'g')
                    {
                        SetLabelText("1k Bytes", FlashSize);
                        FlashSizeInt = 1 * 1024;
                    }
                    if (ch == 'h')
                    {
                        SetLabelText("2k Bytes", FlashSize);
                        FlashSizeInt = 2 * 1024;
                    }
                    if (ch == 'i')
                    {
                        SetLabelText("4k Bytes", FlashSize);
                        FlashSizeInt = 4 * 1024;
                    }
                    if (ch == 'l')
                    {
                        SetLabelText("8k Bytes", FlashSize);
                        FlashSizeInt = 8 * 1024;
                    }
                    if (ch == 'm')
                    {
                        SetLabelText("16k Bytes", FlashSize);
                        FlashSizeInt = 16 * 1024;
                    }
                    if (ch == 'n')
                    {
                        SetLabelText("32k Bytes", FlashSize);
                        FlashSizeInt = 32 * 1024;
                    }
                    if (ch == 'o')
                    {
                        SetLabelText("64k Bytes", FlashSize);
                        FlashSizeInt = 64 * 1024;
                    }
                    if (ch == 'p')
                    {
                        SetLabelText("128k Bytes", FlashSize);
                        FlashSizeInt = 128 * 1024;
                    }
                    if (ch == 'q')
                    {
                        SetLabelText("256k Bytes", FlashSize);
                        FlashSizeInt = 256 * 1024;
                    }
                    if (ch == 'r')
                    {
                        SetLabelText("40k Bytes", FlashSize);
                        FlashSizeInt = 40 * 1024;
                    }


                    // 1-5 for EEpromSize
                    if (ch == '.')
                    {
                        SetLabelText("64 Bytes", EEpromSize);
                        EEpromSizeInt = 512;
                    }
                    if (ch == '/')
                    {
                        SetLabelText("128 Bytes", EEpromSize);
                        EEpromSizeInt = 512;
                    }
                    if (ch == '0')
                    {
                        SetLabelText("256 Bytes", EEpromSize);
                        EEpromSizeInt = 512;
                    }
                    if (ch == '1')
                    {
                        SetLabelText("512 Bytes", EEpromSize);
                        EEpromSizeInt = 512;
                    }
                    if (ch == '2')
                    {
                        SetLabelText("1k Bytes", EEpromSize);
                        EEpromSizeInt = 1 * 1024;
                    }
                    if (ch == '3')
                    {
                        SetLabelText("2k Bytes", EEpromSize);
                        EEpromSizeInt = 2 * 1024;
                    }
                    if (ch == '4')
                    {
                        SetLabelText("4k Bytes", EEpromSize);
                        EEpromSizeInt = 4 * 1024;
                    }
                }
            }
            catch (Exception Ex)
            {
            }
		}

		private void Monitor_Click(object sender, System.EventArgs e)
		{
			Monitor = new MonitorForm(this.Comm1);
			Monitor.Visible = true;
		}

		private void PortOpenClose_Click(object sender, System.EventArgs e)
		{
			if (PortOpenClose.Text == "Close Port")
			{
                Comm1.Close();
				PortOpenClose.Text = "Open Port";
			}
			else
			{
				try
				{
                    Comm1.Open();
					PortOpenClose.Text = "Close Port";
				}
				catch
				{
				}
			}

		}

		private void AboutBoutton_Click(object sender, System.EventArgs e)
		{
			About = new AboutForm(this,this.Comm1,Reg);
			About.Visible = true;
		}
	}
}
