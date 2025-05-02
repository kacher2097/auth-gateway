package com.authenhub.service.impl;

import com.authenhub.bean.tool.codegenerator.CodeGeneratorRequest;
import com.authenhub.bean.tool.codegenerator.CodeGeneratorResponse;
import com.authenhub.service.interfaces.ICodeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeGeneratorService implements ICodeGeneratorService {

    @Override
    public CodeGeneratorResponse generateCode(CodeGeneratorRequest request) {
        log.info("Generating code of type: {}", request.getCodeType());
        
        String generatedCode = "";
        String language = "javascript";
        String fileName = "generated-code.js";
        
        if ("crud".equals(request.getCodeType())) {
            generatedCode = generateCrudCode(request);
            fileName = request.getModelName().toLowerCase() + ".js";
        } else if ("component".equals(request.getCodeType())) {
            generatedCode = generateComponentCode(request);
            fileName = request.getComponentName() + ".tsx";
            language = "typescript";
        } else if ("api".equals(request.getCodeType())) {
            generatedCode = generateApiCode(request);
            fileName = request.getApiName() + "-routes.js";
        }
        
        return CodeGeneratorResponse.builder()
                .code(generatedCode)
                .language(language)
                .fileName(fileName)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .codeType(request.getCodeType())
                .framework(request.getFramework())
                .build();
    }

    private String generateCrudCode(CodeGeneratorRequest request) {
        StringBuilder sb = new StringBuilder();
        
        // Generate model
        sb.append("// Model definition\n");
        sb.append("const ").append(request.getModelName()).append(" = sequelize.define('")
                .append(request.getModelName().toLowerCase()).append("', {\n");
        sb.append("  id: {\n");
        sb.append("    type: DataTypes.INTEGER,\n");
        sb.append("    primaryKey: true,\n");
        sb.append("    autoIncrement: true\n");
        sb.append("  },\n");
        
        // Parse fields
        if (request.getFields() != null && !request.getFields().isEmpty()) {
            String[] fields = request.getFields().split("\\n");
            for (String field : fields) {
                String[] parts = field.trim().split(":");
                if (parts.length == 2) {
                    String fieldName = parts[0].trim();
                    String fieldType = parts[1].trim();
                    
                    sb.append("  ").append(fieldName).append(": {\n");
                    sb.append("    type: DataTypes.").append(fieldType.toUpperCase()).append(",\n");
                    sb.append("    allowNull: false\n");
                    sb.append("  },\n");
                }
            }
        } else {
            // Default fields
            sb.append("  name: {\n");
            sb.append("    type: DataTypes.STRING,\n");
            sb.append("    allowNull: false\n");
            sb.append("  },\n");
            sb.append("  description: {\n");
            sb.append("    type: DataTypes.TEXT\n");
            sb.append("  },\n");
        }
        
        sb.append("});\n\n");
        
        // Generate controller
        sb.append("// Controller\n");
        sb.append("class ").append(request.getModelName()).append("Controller {\n");
        
        // Get all
        sb.append("  // Get all\n");
        sb.append("  async getAll(req, res) {\n");
        sb.append("    try {\n");
        sb.append("      const items = await ").append(request.getModelName()).append(".findAll();\n");
        sb.append("      return res.status(200).json(items);\n");
        sb.append("    } catch (error) {\n");
        sb.append("      return res.status(500).json({ error: error.message });\n");
        sb.append("    }\n");
        sb.append("  }\n\n");
        
        // Get by ID
        sb.append("  // Get by ID\n");
        sb.append("  async getById(req, res) {\n");
        sb.append("    try {\n");
        sb.append("      const item = await ").append(request.getModelName()).append(".findByPk(req.params.id);\n");
        sb.append("      if (!item) {\n");
        sb.append("        return res.status(404).json({ error: 'Item not found' });\n");
        sb.append("      }\n");
        sb.append("      return res.status(200).json(item);\n");
        sb.append("    } catch (error) {\n");
        sb.append("      return res.status(500).json({ error: error.message });\n");
        sb.append("    }\n");
        sb.append("  }\n\n");
        
        // Create
        sb.append("  // Create\n");
        sb.append("  async create(req, res) {\n");
        sb.append("    try {\n");
        sb.append("      const item = await ").append(request.getModelName()).append(".create(req.body);\n");
        sb.append("      return res.status(201).json(item);\n");
        sb.append("    } catch (error) {\n");
        sb.append("      return res.status(500).json({ error: error.message });\n");
        sb.append("    }\n");
        sb.append("  }\n\n");
        
        // Update
        sb.append("  // Update\n");
        sb.append("  async update(req, res) {\n");
        sb.append("    try {\n");
        sb.append("      const [updated] = await ").append(request.getModelName()).append(".update(req.body, {\n");
        sb.append("        where: { id: req.params.id }\n");
        sb.append("      });\n");
        sb.append("      if (updated) {\n");
        sb.append("        const updatedItem = await ").append(request.getModelName()).append(".findByPk(req.params.id);\n");
        sb.append("        return res.status(200).json(updatedItem);\n");
        sb.append("      }\n");
        sb.append("      return res.status(404).json({ error: 'Item not found' });\n");
        sb.append("    } catch (error) {\n");
        sb.append("      return res.status(500).json({ error: error.message });\n");
        sb.append("    }\n");
        sb.append("  }\n\n");
        
        // Delete
        sb.append("  // Delete\n");
        sb.append("  async delete(req, res) {\n");
        sb.append("    try {\n");
        sb.append("      const deleted = await ").append(request.getModelName()).append(".destroy({\n");
        sb.append("        where: { id: req.params.id }\n");
        sb.append("      });\n");
        sb.append("      if (deleted) {\n");
        sb.append("        return res.status(204).send();\n");
        sb.append("      }\n");
        sb.append("      return res.status(404).json({ error: 'Item not found' });\n");
        sb.append("    } catch (error) {\n");
        sb.append("      return res.status(500).json({ error: error.message });\n");
        sb.append("    }\n");
        sb.append("  }\n");
        sb.append("}\n\n");
        
        // Generate routes
        sb.append("// Routes\n");
        sb.append("const router = express.Router();\n");
        sb.append("const controller = new ").append(request.getModelName()).append("Controller();\n\n");
        sb.append("router.get('/").append(request.getModelName().toLowerCase()).append("s', controller.getAll);\n");
        sb.append("router.get('/").append(request.getModelName().toLowerCase()).append("s/:id', controller.getById);\n");
        sb.append("router.post('/").append(request.getModelName().toLowerCase()).append("s', controller.create);\n");
        sb.append("router.put('/").append(request.getModelName().toLowerCase()).append("s/:id', controller.update);\n");
        sb.append("router.delete('/").append(request.getModelName().toLowerCase()).append("s/:id', controller.delete);\n\n");
        sb.append("module.exports = router;");
        
        return sb.toString();
    }

    private String generateComponentCode(CodeGeneratorRequest request) {
        StringBuilder sb = new StringBuilder();
        
        // Imports
        sb.append("import React, { useState, useEffect } from 'react';\n");
        sb.append("import { Card, Button, Form, Input, Space, Table, Modal, message } from 'antd';\n\n");
        
        // Interface
        sb.append("interface ").append(request.getComponentName()).append("Props {\n");
        sb.append("  title?: string;\n");
        sb.append("}\n\n");
        
        // Data interface
        sb.append("interface DataItem {\n");
        sb.append("  id: string;\n");
        sb.append("  name: string;\n");
        sb.append("  description?: string;\n");
        sb.append("  createdAt: string;\n");
        sb.append("}\n\n");
        
        // Component
        if ("functional".equals(request.getComponentType())) {
            sb.append("const ").append(request.getComponentName()).append(": React.FC<").append(request.getComponentName()).append("Props> = ({ title = '").append(request.getComponentName()).append("' }) => {\n");
            sb.append("  const [data, setData] = useState<DataItem[]>([]);\n");
            sb.append("  const [loading, setLoading] = useState<boolean>(false);\n");
            sb.append("  const [modalVisible, setModalVisible] = useState<boolean>(false);\n");
            sb.append("  const [editingItem, setEditingItem] = useState<DataItem | null>(null);\n");
            sb.append("  const [form] = Form.useForm();\n\n");
            
            // useEffect
            sb.append("  useEffect(() => {\n");
            sb.append("    fetchData();\n");
            sb.append("  }, []);\n\n");
            
            // fetchData
            sb.append("  const fetchData = async () => {\n");
            sb.append("    setLoading(true);\n");
            sb.append("    try {\n");
            sb.append("      // Replace with actual API call\n");
            sb.append("      const response = await fetch('/api/items');\n");
            sb.append("      const result = await response.json();\n");
            sb.append("      setData(result);\n");
            sb.append("    } catch (error) {\n");
            sb.append("      message.error('Failed to fetch data');\n");
            sb.append("      console.error(error);\n");
            sb.append("    } finally {\n");
            sb.append("      setLoading(false);\n");
            sb.append("    }\n");
            sb.append("  };\n\n");
            
            // handleAdd
            sb.append("  const handleAdd = () => {\n");
            sb.append("    setEditingItem(null);\n");
            sb.append("    form.resetFields();\n");
            sb.append("    setModalVisible(true);\n");
            sb.append("  };\n\n");
            
            // handleEdit
            sb.append("  const handleEdit = (record: DataItem) => {\n");
            sb.append("    setEditingItem(record);\n");
            sb.append("    form.setFieldsValue(record);\n");
            sb.append("    setModalVisible(true);\n");
            sb.append("  };\n\n");
            
            // handleDelete
            sb.append("  const handleDelete = async (id: string) => {\n");
            sb.append("    try {\n");
            sb.append("      // Replace with actual API call\n");
            sb.append("      await fetch(`/api/items/${id}`, { method: 'DELETE' });\n");
            sb.append("      message.success('Item deleted successfully');\n");
            sb.append("      fetchData();\n");
            sb.append("    } catch (error) {\n");
            sb.append("      message.error('Failed to delete item');\n");
            sb.append("      console.error(error);\n");
            sb.append("    }\n");
            sb.append("  };\n\n");
            
            // handleSubmit
            sb.append("  const handleSubmit = async (values: any) => {\n");
            sb.append("    try {\n");
            sb.append("      if (editingItem) {\n");
            sb.append("        // Update existing item\n");
            sb.append("        await fetch(`/api/items/${editingItem.id}`, {\n");
            sb.append("          method: 'PUT',\n");
            sb.append("          headers: { 'Content-Type': 'application/json' },\n");
            sb.append("          body: JSON.stringify(values)\n");
            sb.append("        });\n");
            sb.append("        message.success('Item updated successfully');\n");
            sb.append("      } else {\n");
            sb.append("        // Create new item\n");
            sb.append("        await fetch('/api/items', {\n");
            sb.append("          method: 'POST',\n");
            sb.append("          headers: { 'Content-Type': 'application/json' },\n");
            sb.append("          body: JSON.stringify(values)\n");
            sb.append("        });\n");
            sb.append("        message.success('Item created successfully');\n");
            sb.append("      }\n");
            sb.append("      setModalVisible(false);\n");
            sb.append("      fetchData();\n");
            sb.append("    } catch (error) {\n");
            sb.append("      message.error('Failed to save item');\n");
            sb.append("      console.error(error);\n");
            sb.append("    }\n");
            sb.append("  };\n\n");
            
            // columns
            sb.append("  const columns = [\n");
            sb.append("    {\n");
            sb.append("      title: 'Name',\n");
            sb.append("      dataIndex: 'name',\n");
            sb.append("      key: 'name',\n");
            sb.append("    },\n");
            sb.append("    {\n");
            sb.append("      title: 'Description',\n");
            sb.append("      dataIndex: 'description',\n");
            sb.append("      key: 'description',\n");
            sb.append("    },\n");
            sb.append("    {\n");
            sb.append("      title: 'Created At',\n");
            sb.append("      dataIndex: 'createdAt',\n");
            sb.append("      key: 'createdAt',\n");
            sb.append("    },\n");
            sb.append("    {\n");
            sb.append("      title: 'Actions',\n");
            sb.append("      key: 'actions',\n");
            sb.append("      render: (text: string, record: DataItem) => (\n");
            sb.append("        <Space>\n");
            sb.append("          <Button type=\"link\" onClick={() => handleEdit(record)}>Edit</Button>\n");
            sb.append("          <Button type=\"link\" danger onClick={() => handleDelete(record.id)}>Delete</Button>\n");
            sb.append("        </Space>\n");
            sb.append("      ),\n");
            sb.append("    },\n");
            sb.append("  ];\n\n");
            
            // render
            sb.append("  return (\n");
            sb.append("    <Card title={title}>\n");
            sb.append("      <Button type=\"primary\" onClick={handleAdd} style={{ marginBottom: 16 }}>\n");
            sb.append("        Add Item\n");
            sb.append("      </Button>\n");
            sb.append("      <Table\n");
            sb.append("        columns={columns}\n");
            sb.append("        dataSource={data}\n");
            sb.append("        rowKey=\"id\"\n");
            sb.append("        loading={loading}\n");
            sb.append("      />\n");
            sb.append("      <Modal\n");
            sb.append("        title={editingItem ? 'Edit Item' : 'Add Item'}\n");
            sb.append("        visible={modalVisible}\n");
            sb.append("        onCancel={() => setModalVisible(false)}\n");
            sb.append("        footer={null}\n");
            sb.append("      >\n");
            sb.append("        <Form form={form} onFinish={handleSubmit} layout=\"vertical\">\n");
            sb.append("          <Form.Item\n");
            sb.append("            name=\"name\"\n");
            sb.append("            label=\"Name\"\n");
            sb.append("            rules={[{ required: true, message: 'Please enter a name' }]}\n");
            sb.append("          >\n");
            sb.append("            <Input />\n");
            sb.append("          </Form.Item>\n");
            sb.append("          <Form.Item\n");
            sb.append("            name=\"description\"\n");
            sb.append("            label=\"Description\"\n");
            sb.append("          >\n");
            sb.append("            <Input.TextArea rows={4} />\n");
            sb.append("          </Form.Item>\n");
            sb.append("          <Form.Item>\n");
            sb.append("            <Button type=\"primary\" htmlType=\"submit\">\n");
            sb.append("              {editingItem ? 'Update' : 'Create'}\n");
            sb.append("            </Button>\n");
            sb.append("          </Form.Item>\n");
            sb.append("        </Form>\n");
            sb.append("      </Modal>\n");
            sb.append("    </Card>\n");
            sb.append("  );\n");
            sb.append("};\n\n");
        } else {
            // Class component
            sb.append("class ").append(request.getComponentName()).append(" extends React.Component<").append(request.getComponentName()).append("Props, any> {\n");
            sb.append("  constructor(props: ").append(request.getComponentName()).append("Props) {\n");
            sb.append("    super(props);\n");
            sb.append("    this.state = {\n");
            sb.append("      data: [],\n");
            sb.append("      loading: false,\n");
            sb.append("      modalVisible: false,\n");
            sb.append("      editingItem: null\n");
            sb.append("    };\n");
            sb.append("  }\n\n");
            
            // componentDidMount
            sb.append("  componentDidMount() {\n");
            sb.append("    this.fetchData();\n");
            sb.append("  }\n\n");
            
            // fetchData
            sb.append("  fetchData = async () => {\n");
            sb.append("    this.setState({ loading: true });\n");
            sb.append("    try {\n");
            sb.append("      // Replace with actual API call\n");
            sb.append("      const response = await fetch('/api/items');\n");
            sb.append("      const result = await response.json();\n");
            sb.append("      this.setState({ data: result });\n");
            sb.append("    } catch (error) {\n");
            sb.append("      message.error('Failed to fetch data');\n");
            sb.append("      console.error(error);\n");
            sb.append("    } finally {\n");
            sb.append("      this.setState({ loading: false });\n");
            sb.append("    }\n");
            sb.append("  };\n\n");
            
            // render
            sb.append("  render() {\n");
            sb.append("    const { title = '").append(request.getComponentName()).append("' } = this.props;\n");
            sb.append("    const { data, loading } = this.state;\n\n");
            sb.append("    const columns = [\n");
            sb.append("      {\n");
            sb.append("        title: 'Name',\n");
            sb.append("        dataIndex: 'name',\n");
            sb.append("        key: 'name',\n");
            sb.append("      },\n");
            sb.append("      {\n");
            sb.append("        title: 'Description',\n");
            sb.append("        dataIndex: 'description',\n");
            sb.append("        key: 'description',\n");
            sb.append("      },\n");
            sb.append("      {\n");
            sb.append("        title: 'Created At',\n");
            sb.append("        dataIndex: 'createdAt',\n");
            sb.append("        key: 'createdAt',\n");
            sb.append("      }\n");
            sb.append("    ];\n\n");
            sb.append("    return (\n");
            sb.append("      <Card title={title}>\n");
            sb.append("        <Table\n");
            sb.append("          columns={columns}\n");
            sb.append("          dataSource={data}\n");
            sb.append("          rowKey=\"id\"\n");
            sb.append("          loading={loading}\n");
            sb.append("        />\n");
            sb.append("      </Card>\n");
            sb.append("    );\n");
            sb.append("  }\n");
            sb.append("}\n\n");
        }
        
        sb.append("export default ").append(request.getComponentName()).append(";");
        
        return sb.toString();
    }

    private String generateApiCode(CodeGeneratorRequest request) {
        StringBuilder sb = new StringBuilder();
        
        // Express.js API
        if ("express".equals(request.getFramework())) {
            sb.append("const express = require('express');\n");
            sb.append("const router = express.Router();\n");
            if (request.getAuthRequired()) {
                sb.append("const authMiddleware = require('../middleware/auth');\n");
            }
            sb.append("\n");
            
            // GET all
            sb.append("/**\n");
            sb.append(" * @route   GET /api/").append(request.getApiName()).append("\n");
            sb.append(" * @desc    Get all ").append(request.getApiName()).append("\n");
            sb.append(" * @access  ").append(request.getAuthRequired() ? "Private" : "Public").append("\n");
            sb.append(" */\n");
            sb.append("router.get('/'");
            if (request.getAuthRequired()) {
                sb.append(", authMiddleware");
            }
            sb.append(", async (req, res) => {\n");
            sb.append("  try {\n");
            sb.append("    // TODO: Implement data retrieval logic\n");
            sb.append("    const items = []; // Replace with actual data\n");
            sb.append("    res.json(items);\n");
            sb.append("  } catch (err) {\n");
            sb.append("    console.error(err.message);\n");
            sb.append("    res.status(500).send('Server Error');\n");
            sb.append("  }\n");
            sb.append("});\n\n");
            
            // GET by ID
            sb.append("/**\n");
            sb.append(" * @route   GET /api/").append(request.getApiName()).append("/:id\n");
            sb.append(" * @desc    Get ").append(request.getApiName()).append(" by ID\n");
            sb.append(" * @access  ").append(request.getAuthRequired() ? "Private" : "Public").append("\n");
            sb.append(" */\n");
            sb.append("router.get('/:id'");
            if (request.getAuthRequired()) {
                sb.append(", authMiddleware");
            }
            sb.append(", async (req, res) => {\n");
            sb.append("  try {\n");
            sb.append("    // TODO: Implement data retrieval logic\n");
            sb.append("    const item = {}; // Replace with actual data\n");
            sb.append("    if (!item) {\n");
            sb.append("      return res.status(404).json({ msg: 'Item not found' });\n");
            sb.append("    }\n");
            sb.append("    res.json(item);\n");
            sb.append("  } catch (err) {\n");
            sb.append("    console.error(err.message);\n");
            sb.append("    res.status(500).send('Server Error');\n");
            sb.append("  }\n");
            sb.append("});\n\n");
            
            // POST
            sb.append("/**\n");
            sb.append(" * @route   POST /api/").append(request.getApiName()).append("\n");
            sb.append(" * @desc    Create a new ").append(request.getApiName()).append("\n");
            sb.append(" * @access  ").append(request.getAuthRequired() ? "Private" : "Public").append("\n");
            sb.append(" */\n");
            sb.append("router.post('/'");
            if (request.getAuthRequired()) {
                sb.append(", authMiddleware");
            }
            sb.append(", async (req, res) => {\n");
            sb.append("  try {\n");
            sb.append("    // TODO: Implement data creation logic\n");
            sb.append("    const newItem = {}; // Replace with actual data\n");
            sb.append("    res.json(newItem);\n");
            sb.append("  } catch (err) {\n");
            sb.append("    console.error(err.message);\n");
            sb.append("    res.status(500).send('Server Error');\n");
            sb.append("  }\n");
            sb.append("});\n\n");
            
            // PUT
            sb.append("/**\n");
            sb.append(" * @route   PUT /api/").append(request.getApiName()).append("/:id\n");
            sb.append(" * @desc    Update ").append(request.getApiName()).append(" by ID\n");
            sb.append(" * @access  ").append(request.getAuthRequired() ? "Private" : "Public").append("\n");
            sb.append(" */\n");
            sb.append("router.put('/:id'");
            if (request.getAuthRequired()) {
                sb.append(", authMiddleware");
            }
            sb.append(", async (req, res) => {\n");
            sb.append("  try {\n");
            sb.append("    // TODO: Implement data update logic\n");
            sb.append("    const updatedItem = {}; // Replace with actual data\n");
            sb.append("    if (!updatedItem) {\n");
            sb.append("      return res.status(404).json({ msg: 'Item not found' });\n");
            sb.append("    }\n");
            sb.append("    res.json(updatedItem);\n");
            sb.append("  } catch (err) {\n");
            sb.append("    console.error(err.message);\n");
            sb.append("    res.status(500).send('Server Error');\n");
            sb.append("  }\n");
            sb.append("});\n\n");
            
            // DELETE
            sb.append("/**\n");
            sb.append(" * @route   DELETE /api/").append(request.getApiName()).append("/:id\n");
            sb.append(" * @desc    Delete ").append(request.getApiName()).append(" by ID\n");
            sb.append(" * @access  ").append(request.getAuthRequired() ? "Private" : "Public").append("\n");
            sb.append(" */\n");
            sb.append("router.delete('/:id'");
            if (request.getAuthRequired()) {
                sb.append(", authMiddleware");
            }
            sb.append(", async (req, res) => {\n");
            sb.append("  try {\n");
            sb.append("    // TODO: Implement data deletion logic\n");
            sb.append("    res.json({ msg: 'Item removed' });\n");
            sb.append("  } catch (err) {\n");
            sb.append("    console.error(err.message);\n");
            sb.append("    res.status(500).send('Server Error');\n");
            sb.append("  }\n");
            sb.append("});\n\n");
            
            sb.append("module.exports = router;");
        }
        
        return sb.toString();
    }

    @Override
    public byte[] exportGeneratedCode(CodeGeneratorResponse response) {
        return response.getCode().getBytes();
    }
}
